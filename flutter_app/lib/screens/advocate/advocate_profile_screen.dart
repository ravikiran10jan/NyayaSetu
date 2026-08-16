import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme.dart';
import '../../providers/auth_provider.dart';

class AdvocateProfileScreen extends StatelessWidget {
  const AdvocateProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = context.watch<AuthProvider>();
    final user = authProvider.user;

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
      ),
      body: user == null
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  const SizedBox(height: 20),
                  CircleAvatar(
                    radius: 50,
                    backgroundColor: AppTheme.saffron,
                    child: Text(
                      _getInitials(user.name),
                      style: const TextStyle(
                        fontSize: 32,
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    user.name,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(
                      color: user.isVerified ? AppTheme.green.withOpacity(0.1) : Colors.orange.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          user.isVerified ? Icons.verified : Icons.pending,
                          size: 16,
                          color: user.isVerified ? AppTheme.green : Colors.orange,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          user.isVerified ? 'Verified' : 'Pending Verification',
                          style: TextStyle(
                            color: user.isVerified ? AppTheme.green : Colors.orange,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 32),
                  _buildInfoCard(
                    'Email',
                    user.email,
                    Icons.email,
                  ),
                  const SizedBox(height: 12),
                  _buildInfoCard(
                    'Role',
                    user.role.toUpperCase(),
                    Icons.work,
                  ),
                  if (user.practiceArea != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      'Practice Area',
                      user.practiceArea!,
                      Icons.gavel,
                    ),
                  ],
                  if (user.barCouncilId != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      'Bar Council ID',
                      user.barCouncilId!,
                      Icons.badge,
                    ),
                  ],
                  if (user.jurisdictionState != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      'Jurisdiction',
                      user.jurisdictionState!,
                      Icons.location_on,
                    ),
                  ],
                  if (user.city != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      'City',
                      user.city!,
                      Icons.location_city,
                    ),
                  ],
                  if (user.languages.isNotEmpty) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      'Languages',
                      user.languages.join(', '),
                      Icons.language,
                    ),
                  ],
                  const SizedBox(height: 32),
                  SizedBox(
                    width: double.infinity,
                    child: OutlinedButton(
                      onPressed: () {
                        authProvider.signOut();
                        Navigator.pushNamedAndRemoveUntil(context, '/login', (route) => false);
                      },
                      style: OutlinedButton.styleFrom(
                        foregroundColor: Colors.red,
                        side: const BorderSide(color: Colors.red),
                        padding: const EdgeInsets.symmetric(vertical: 16),
                      ),
                      child: const Text('Logout'),
                    ),
                  ),
                ],
              ),
            ),
    );
  }

  Widget _buildInfoCard(String label, String value, IconData icon) {
    return Card(
      child: ListTile(
        leading: Icon(icon, color: AppTheme.saffron),
        title: Text(
          label,
          style: TextStyle(color: Colors.grey[600], fontSize: 12),
        ),
        subtitle: Text(
          value,
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
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
