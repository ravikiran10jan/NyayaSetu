import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../core/theme.dart';
import '../../providers/auth_provider.dart';
import '../../providers/bookings_provider.dart';
import '../../widgets/primary_button.dart';

class BookingScreen extends StatefulWidget {
  final String advocateId;
  final String advocateName;
  final String initialMode;

  const BookingScreen({
    super.key,
    required this.advocateId,
    required this.advocateName,
    this.initialMode = 'chat',
  });

  @override
  State<BookingScreen> createState() => _BookingScreenState();
}

class _BookingScreenState extends State<BookingScreen> {
  String _mode = 'chat';
  DateTime? _selectedDateTime;
  final _clientNoteController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _isSubmitting = false;

  @override
  void initState() {
    super.initState();
    _mode = widget.initialMode;
  }

  @override
  void dispose() {
    _clientNoteController.dispose();
    super.dispose();
  }

  Future<void> _selectDateTime(BuildContext context) async {
    final now = DateTime.now();
    final minDate = now.add(const Duration(minutes: 15));
    final maxDate = now.add(const Duration(days: 90));

    final date = await showDatePicker(
      context: context,
      initialDate: minDate,
      firstDate: minDate,
      lastDate: maxDate,
    );

    if (date == null || !mounted) return;

    final time = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.fromDateTime(minDate),
    );

    if (time == null || !mounted) return;

    final selected = DateTime(
      date.year,
      date.month,
      date.day,
      time.hour,
      time.minute,
    );

    if (selected.isBefore(minDate)) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a time at least 15 minutes from now')),
      );
      return;
    }

    if (selected.isAfter(maxDate)) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a date within 90 days')),
      );
      return;
    }

    setState(() {
      _selectedDateTime = selected;
    });
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    if (_selectedDateTime == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a date and time')),
      );
      return;
    }

    setState(() => _isSubmitting = true);

    final authProvider = context.read<AuthProvider>();
    final bookingsProvider = context.read<BookingsProvider>();

    try {
      await bookingsProvider.createBooking(
        advocateId: widget.advocateId,
        advocateName: widget.advocateName,
        clientId: authProvider.user!.uid,
        mode: _mode,
        scheduledStartsAt: _selectedDateTime!,
        clientNote: _clientNoteController.text.isNotEmpty
            ? _clientNoteController.text
            : null,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Booking submitted successfully'),
            backgroundColor: AppTheme.green,
          ),
        );
        context.go('/consultations');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to create booking: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isSubmitting = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Book Consultation'),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildAdvocateInfo(),
              const SizedBox(height: 24),
              const Text(
                'Consultation Mode',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 12),
              ToggleButtons(
                selectedIndex: _mode == 'chat' ? 0 : 1,
                onPressed: (index) {
                  setState(() {
                    _mode = index == 0 ? 'chat' : 'call';
                  });
                },
                isSelected: [_mode == 'chat', _mode == 'call'],
                constraints: BoxConstraints(
                  minWidth: (MediaQuery.of(context).size.width - 48) / 2,
                ),
                children: const [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.chat),
                      SizedBox(width: 8),
                      Text('Chat'),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.call),
                      SizedBox(width: 8),
                      Text('Call'),
                    ],
                  ),
                ],
              ),
              const SizedBox(height: 24),
              const Text(
                'Schedule Date & Time',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 12),
              InkWell(
                onTap: () => _selectDateTime(context),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.calendar_today, color: AppTheme.saffron),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Text(
                          _selectedDateTime != null
                              ? DateFormat('EEE, MMM d, yyyy • h:mm a').format(_selectedDateTime!)
                              : 'Select date and time',
                          style: TextStyle(
                            color: _selectedDateTime != null ? Colors.black : Colors.grey,
                          ),
                        ),
                      ),
                      const Icon(Icons.arrow_drop_down),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Must be between 15 minutes and 90 days from now',
                style: TextStyle(fontSize: 12, color: Colors.grey[600]),
              ),
              const SizedBox(height: 24),
              const Text(
                'Client Note (Optional)',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _clientNoteController,
                maxLines: 4,
                decoration: const InputDecoration(
                  hintText: 'Describe your legal issue briefly...',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 32),
              PrimaryButton(
                text: 'Submit Booking',
                isLoading: _isSubmitting,
                onPressed: _submit,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAdvocateInfo() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            CircleAvatar(
              radius: 28,
              backgroundColor: AppTheme.saffron,
              child: Text(
                _getInitials(widget.advocateName),
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.advocateName,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Advocate',
                    style: TextStyle(color: Colors.grey[600]),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getInitials(String name) {
    final parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    }
    return name.isNotEmpty ? name[0].toUpperCase() : '?';
  }
}
