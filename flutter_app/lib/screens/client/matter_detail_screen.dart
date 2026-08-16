import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../core/theme.dart';
import '../../core/constants.dart';
import '../../providers/matters_provider.dart';
import '../../providers/auth_provider.dart';
import '../../widgets/matter_record_tile.dart';

class MatterDetailScreen extends StatefulWidget {
  final String matterId;

  const MatterDetailScreen({super.key, required this.matterId});

  @override
  State<MatterDetailScreen> createState() => _MatterDetailScreenState();
}

class _MatterDetailScreenState extends State<MatterDetailScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<MattersProvider>().loadRecords(widget.matterId);
    });
  }

  void _showAddRecordDialog() {
    final titleController = TextEditingController();
    final descriptionController = TextEditingController();
    String selectedType = 'note';

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) => AlertDialog(
          title: const Text('Add Record'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                DropdownButtonFormField<String>(
                  value: selectedType,
                  decoration: const InputDecoration(labelText: 'Type'),
                  items: const [
                    DropdownMenuItem(value: 'note', child: Text('Note')),
                    DropdownMenuItem(value: 'task', child: Text('Task')),
                    DropdownMenuItem(value: 'advice', child: Text('Advice')),
                    DropdownMenuItem(value: 'document_request', child: Text('Document Request')),
                    DropdownMenuItem(value: 'timeline', child: Text('Timeline')),
                  ],
                  onChanged: (value) {
                    setState(() {
                      selectedType = value!;
                    });
                  },
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: titleController,
                  decoration: const InputDecoration(
                    labelText: 'Title',
                    hintText: 'Enter title',
                  ),
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: descriptionController,
                  maxLines: 3,
                  decoration: const InputDecoration(
                    labelText: 'Description (Optional)',
                    hintText: 'Enter description',
                  ),
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
                  await mattersProvider.addRecord(
                    matterId: widget.matterId,
                    type: selectedType,
                    title: titleController.text,
                    description: descriptionController.text.isNotEmpty
                        ? descriptionController.text
                        : null,
                    createdBy: authProvider.user!.uid,
                  );

                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Record added successfully'),
                      backgroundColor: AppTheme.green,
                    ),
                  );
                } catch (e) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Error: $e')),
                  );
                }
              },
              child: const Text('Add'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final mattersProvider = context.watch<MattersProvider>();
    final matter = mattersProvider.matters.firstWhere((m) => m.id == widget.matterId);
    final records = mattersProvider.records[widget.matterId] ?? [];

    return Scaffold(
      appBar: AppBar(
        title: Text(matter.title),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () => _showEditMatterDialog(matter),
          ),
        ],
      ),
      body: Column(
        children: [
          _buildMatterInfo(matter),
          const Divider(),
          Expanded(
            child: records.isEmpty
                ? const Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.timeline, size: 64, color: Colors.grey),
                        SizedBox(height: 16),
                        Text(
                          'No records yet',
                          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                        ),
                        SizedBox(height: 8),
                        Text(
                          'Add notes, tasks, or advice',
                          style: TextStyle(color: Colors.grey),
                        ),
                      ],
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.symmetric(vertical: 8),
                    itemCount: records.length,
                    itemBuilder: (context, index) {
                      return MatterRecordTile(record: records[index]);
                    },
                  ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _showAddRecordDialog,
        backgroundColor: AppTheme.saffron,
        icon: const Icon(Icons.add),
        label: const Text('Add Record'),
      ),
    );
  }

  Widget _buildMatterInfo(matter) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      matter.practiceArea,
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: AppTheme.saffron,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${matter.state} • ${matter.language}',
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                  ],
                ),
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(
                      color: _getUrgencyColor(matter.urgency).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Text(
                      matter.urgency.toUpperCase(),
                      style: TextStyle(
                        color: _getUrgencyColor(matter.urgency),
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  if (matter.hasConflict) ...[
                    const SizedBox(height: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                      decoration: BoxDecoration(
                        color: Colors.red.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: const Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.warning, size: 14, color: Colors.red),
                          SizedBox(width: 4),
                          Text(
                            'Conflict',
                            style: TextStyle(
                              color: Colors.red,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ],
              ),
            ],
          ),
          if (matter.cnrNumber != null) ...[
            const SizedBox(height: 12),
            Row(
              children: [
                const Icon(Icons.tag, size: 16, color: AppTheme.navy),
                const SizedBox(width: 8),
                Text(
                  'CNR: ${matter.cnrNumber}',
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
              ],
            ),
          ],
        ],
      ),
    );
  }

  void _showEditMatterDialog(matter) {
    final cnrController = TextEditingController(text: matter.cnrNumber ?? '');

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Edit Matter'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: cnrController,
              decoration: const InputDecoration(
                labelText: 'CNR Number',
                hintText: 'Enter CNR number',
              ),
            ),
            const SizedBox(height: 16),
            SwitchListTile(
              title: const Text('Has Conflict'),
              value: matter.hasConflict,
              onChanged: (value) {
                final mattersProvider = context.read<MattersProvider>();
                mattersProvider.updateMatter(
                  matterId: matter.id,
                  hasConflict: value,
                );
                Navigator.pop(context);
              },
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () async {
              final mattersProvider = context.read<MattersProvider>();
              await mattersProvider.updateMatter(
                matterId: matter.id,
                cnrNumber: cnrController.text.isNotEmpty ? cnrController.text : null,
              );
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Matter updated successfully'),
                  backgroundColor: AppTheme.green,
                ),
              );
            },
            child: const Text('Save'),
          ),
        ],
      ),
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
}
