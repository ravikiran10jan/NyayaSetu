import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../core/theme.dart';
import '../../core/constants.dart';
import '../../providers/matters_provider.dart';
import '../../providers/auth_provider.dart';
import '../../widgets/app_scaffold.dart';

class WorkspaceScreen extends StatefulWidget {
  const WorkspaceScreen({super.key});

  @override
  State<WorkspaceScreen> createState() => _WorkspaceScreenState();
}

class _WorkspaceScreenState extends State<WorkspaceScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<MattersProvider>().loadMatters();
    });
  }

  void _showCreateMatterDialog() {
    final titleController = TextEditingController();
    String selectedArea = AppConstants.practiceAreas.first;
    String selectedState = AppConstants.indianStates.first;
    String selectedLanguage = AppConstants.languages.first;
    String selectedUrgency = 'medium';

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) => AlertDialog(
          title: const Text('Create New Matter'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: titleController,
                  decoration: const InputDecoration(
                    labelText: 'Title',
                    hintText: 'Enter matter title',
                  ),
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: selectedArea,
                  decoration: const InputDecoration(labelText: 'Practice Area'),
                  items: AppConstants.practiceAreas.map((area) {
                    return DropdownMenuItem(value: area, child: Text(area));
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      selectedArea = value!;
                    });
                  },
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: selectedState,
                  decoration: const InputDecoration(labelText: 'State'),
                  items: AppConstants.indianStates.map((state) {
                    return DropdownMenuItem(value: state, child: Text(state));
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      selectedState = value!;
                    });
                  },
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: selectedLanguage,
                  decoration: const InputDecoration(labelText: 'Language'),
                  items: AppConstants.languages.map((lang) {
                    return DropdownMenuItem(value: lang, child: Text(lang));
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      selectedLanguage = value!;
                    });
                  },
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String>(
                  value: selectedUrgency,
                  decoration: const InputDecoration(labelText: 'Urgency'),
                  items: const [
                    DropdownMenuItem(value: 'low', child: Text('Low')),
                    DropdownMenuItem(value: 'medium', child: Text('Medium')),
                    DropdownMenuItem(value: 'high', child: Text('High')),
                    DropdownMenuItem(value: 'critical', child: Text('Critical')),
                  ],
                  onChanged: (value) {
                    setState(() {
                      selectedUrgency = value!;
                    });
                  },
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () async {
                if (titleController.text.isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Please enter a title')),
                  );
                  return;
                }

                final authProvider = context.read<AuthProvider>();
                final mattersProvider = context.read<MattersProvider>();

                try {
                  await mattersProvider.createMatter(
                    title: titleController.text,
                    practiceArea: selectedArea,
                    state: selectedState,
                    language: selectedLanguage,
                    urgency: selectedUrgency,
                    clientId: authProvider.user!.uid,
                  );

                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Matter created successfully'),
                      backgroundColor: AppTheme.green,
                    ),
                  );
                } catch (e) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Error: $e')),
                  );
                }
              },
              child: const Text('Create'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<MattersProvider>();

    return AppScaffold(
      currentIndex: 3,
      title: 'My Matters',
      child: Stack(
        children: [
          _buildMattersList(provider),
          Positioned(
            right: 16,
            bottom: 16,
            child: FloatingActionButton.extended(
              onPressed: _showCreateMatterDialog,
              backgroundColor: AppTheme.saffron,
              icon: const Icon(Icons.add),
              label: const Text('New Matter'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMattersList(MattersProvider provider) {
    if (provider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (provider.error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 48, color: Colors.red),
            const SizedBox(height: 16),
            Text('Error: ${provider.error}'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => provider.loadMatters(),
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    final matters = provider.matters;

    if (matters.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.folder_open, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              'No matters yet',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              'Create a new matter to get started',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: matters.length,
      itemBuilder: (context, index) {
        final matter = matters[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          child: ListTile(
            onTap: () => context.go('/matter/${matter.id}'),
            leading: CircleAvatar(
              backgroundColor: _getUrgencyColor(matter.urgency),
              child: Icon(
                _getUrgencyIcon(matter.urgency),
                color: Colors.white,
              ),
            ),
            title: Text(
              matter.title,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 4),
                Text(matter.practiceArea),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: _getStatusColor(matter.status).withOpacity(0.1),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        matter.status,
                        style: TextStyle(
                          fontSize: 12,
                          color: _getStatusColor(matter.status),
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Text(
                      matter.urgency.toUpperCase(),
                      style: TextStyle(
                        fontSize: 12,
                        color: _getUrgencyColor(matter.urgency),
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    if (matter.hasConflict) ...[
                      const SizedBox(width: 8),
                      const Icon(Icons.warning, size: 16, color: Colors.red),
                    ],
                  ],
                ),
              ],
            ),
            trailing: const Icon(Icons.chevron_right),
          ),
        );
      },
    );
  }

  Color _getUrgencyColor(String urgency) {
    switch (urgency) {
      case 'low':
        return Colors.green;
      case 'medium':
        return Colors.orange;
      case 'high':
        return Colors.deepOrange;
      case 'critical':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  IconData _getUrgencyIcon(String urgency) {
    switch (urgency) {
      case 'low':
        return Icons.trending_down;
      case 'medium':
        return Icons.trending_flat;
      case 'high':
        return Icons.trending_up;
      case 'critical':
        return Icons.error;
      default:
        return Icons.help;
    }
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'active':
        return Colors.blue;
      case 'resolved':
        return AppTheme.green;
      case 'archived':
        return Colors.grey;
      default:
        return Colors.grey;
    }
  }
}
