import 'package:flutter/material.dart';
import 'package:uuid/uuid.dart';
import '../../core/theme.dart';
import '../../services/firestore_service.dart';
import '../../models/anonymous_chat.dart';

class AnonymousChatScreen extends StatefulWidget {
  const AnonymousChatScreen({super.key});

  @override
  State<AnonymousChatScreen> createState() => _AnonymousChatScreenState();
}

class _AnonymousChatScreenState extends State<AnonymousChatScreen> {
  final _aliasController = TextEditingController();
  final _mobileController = TextEditingController();
  final _messageController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  final _firestoreService = FirestoreService();

  bool _consentGiven = false;
  bool _chatStarted = false;
  String _sessionId = '';

  @override
  void dispose() {
    _aliasController.dispose();
    _mobileController.dispose();
    _messageController.dispose();
    super.dispose();
  }

  bool _validateIndianMobile(String mobile) {
    if (mobile.length != 10) return false;
    final firstDigit = int.tryParse(mobile[0]);
    return firstDigit != null && firstDigit >= 6 && firstDigit <= 9;
  }

  void _startChat() {
    if (!_formKey.currentState!.validate()) return;

    if (!_consentGiven) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please provide consent to continue')),
      );
      return;
    }

    if (!_validateIndianMobile(_mobileController.text)) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please enter a valid 10-digit Indian mobile number (starting with 6-9)'),
        ),
      );
      return;
    }

    setState(() {
      _chatStarted = true;
      _sessionId = const Uuid().v4();
    });
  }

  void _sendMessage() {
    if (_messageController.text.trim().isEmpty) return;

    final message = AnonymousChatMessage(
      id: const Uuid().v4(),
      sessionId: _sessionId,
      alias: _aliasController.text,
      mobile: _mobileController.text,
      content: _messageController.text.trim(),
      isFromUser: true,
      timestamp: DateTime.now(),
    );

    _firestoreService.sendChatMessage(message);
    _messageController.clear();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Anonymous Chat'),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
      ),
      body: _chatStarted ? _buildChatView() : _buildStartView(),
    );
  }

  Widget _buildStartView() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Icon(
              Icons.chat_bubble_outline,
              size: 64,
              color: AppTheme.navy,
            ),
            const SizedBox(height: 16),
            const Text(
              'Chat Anonymously',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Get legal guidance without revealing your identity',
              style: TextStyle(color: Colors.grey[600]),
            ),
            const SizedBox(height: 32),
            TextFormField(
              controller: _aliasController,
              decoration: const InputDecoration(
                labelText: 'Alias / Nickname',
                hintText: 'Enter a nickname',
                prefixIcon: Icon(Icons.person_outline),
              ),
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please enter an alias';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _mobileController,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(
                labelText: 'Mobile Number',
                hintText: '+91',
                prefixIcon: Icon(Icons.phone),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter your mobile number';
                }
                return null;
              },
            ),
            const SizedBox(height: 24),
            CheckboxListTile(
              value: _consentGiven,
              onChanged: (value) {
                setState(() {
                  _consentGiven = value ?? false;
                });
              },
              title: const Text(
                'I consent to share my mobile number for verification purposes and understand that my identity will remain anonymous to the advocate.',
              ),
              controlAffinity: ListTileControlAffinity.leading,
              activeColor: AppTheme.saffron,
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _startChat,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppTheme.saffron,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: const Text(
                  'Start Chat',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildChatView() {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(12),
          color: Colors.grey[100],
          child: Row(
            children: [
              CircleAvatar(
                backgroundColor: AppTheme.saffron,
                child: Text(
                  _aliasController.text[0].toUpperCase(),
                  style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _aliasController.text,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    Text(
                      'Anonymous Chat',
                      style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        Expanded(
          child: StreamBuilder<List<AnonymousChatMessage>>(
            stream: _firestoreService.chatMessagesStream(_sessionId),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }

              final messages = snapshot.data ?? [];

              if (messages.isEmpty) {
                return const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.chat_bubble_outline, size: 64, color: Colors.grey),
                      SizedBox(height: 16),
                      Text('Start the conversation'),
                    ],
                  ),
                );
              }

              return ListView.builder(
                reverse: true,
                padding: const EdgeInsets.all(16),
                itemCount: messages.length,
                itemBuilder: (context, index) {
                  final message = messages[messages.length - 1 - index];
                  return _buildMessageBubble(message);
                },
              );
            },
          ),
        ),
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 4,
                offset: const Offset(0, -2),
              ),
            ],
          ),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _messageController,
                  decoration: InputDecoration(
                    hintText: 'Type a message...',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(24),
                    ),
                    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  maxLines: null,
                  textCapitalization: TextCapitalization.sentences,
                ),
              ),
              const SizedBox(width: 8),
              CircleAvatar(
                backgroundColor: AppTheme.saffron,
                child: IconButton(
                  icon: const Icon(Icons.send, color: Colors.white),
                  onPressed: _sendMessage,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildMessageBubble(AnonymousChatMessage message) {
    final isUser = message.isFromUser;
    return Align(
      alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 8),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        decoration: BoxDecoration(
          color: isUser ? AppTheme.saffron : Colors.grey[200],
          borderRadius: BorderRadius.circular(16),
        ),
        child: Text(
          message.content,
          style: TextStyle(
            color: isUser ? Colors.white : Colors.black87,
          ),
        ),
      ),
    );
  }
}
