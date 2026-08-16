import 'package:flutter/material.dart';
import '../../core/theme.dart';

class PrivacyCenterScreen extends StatelessWidget {
  const PrivacyCenterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Privacy Center'),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Icon(
              Icons.privacy_tip,
              size: 64,
              color: AppTheme.navy,
            ),
            const SizedBox(height: 16),
            const Text(
              'Your Privacy Matters',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            _buildSection(
              'Data Collection',
              'NyayaSetu collects only the information necessary to provide legal consultation services. This includes your name, email, and consultation history.',
            ),
            const SizedBox(height: 16),
            _buildSection(
              'Data Usage',
              'Your data is used solely for connecting you with advocates and managing your legal matters. We do not share your information with third parties without your consent.',
            ),
            const SizedBox(height: 16),
            _buildSection(
              'Data Security',
              'We employ industry-standard encryption and security measures to protect your data. All communications are encrypted in transit and at rest.',
            ),
            const SizedBox(height: 16),
            _buildSection(
              'Anonymous Chat',
              'Our anonymous chat feature allows you to seek legal guidance without revealing your identity. Your mobile number is used only for verification purposes.',
            ),
            const SizedBox(height: 16),
            _buildSection(
              'Data Retention',
              'We retain your data only as long as necessary to provide our services. You can request deletion of your data at any time.',
            ),
            const SizedBox(height: 32),
            const Text(
              'Delete Your Data',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.red,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'You have the right to delete all your personal data from our systems. This action is irreversible and will remove all your consultations, matters, and account information.',
              style: TextStyle(color: Colors.grey[700]),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () => _showDeleteConfirmationDialog(context),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: const Text(
                  'Delete My Data',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            const SizedBox(height: 32),
            Center(
              child: Text(
                'Last updated: January 2026',
                style: TextStyle(color: Colors.grey[600], fontSize: 12),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(String title, String content) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              content,
              style: TextStyle(
                color: Colors.grey[700],
                height: 1.5,
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showDeleteConfirmationDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete All Data?'),
        content: const Text(
          'This action cannot be undone. All your personal data, consultations, and matters will be permanently deleted.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Data deletion request submitted. You will receive a confirmation email.'),
                  backgroundColor: AppTheme.green,
                  duration: Duration(seconds: 5),
                ),
              );
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
            ),
            child: const Text('Delete My Data'),
          ),
        ],
      ),
    );
  }
}
