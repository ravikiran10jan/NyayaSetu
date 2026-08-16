import 'package:flutter/material.dart';
import '../core/theme.dart';
import '../models/matter_record.dart';

class MatterRecordTile extends StatelessWidget {
  final MatterRecord record;

  const MatterRecordTile({super.key, required this.record});

  IconData get _icon {
    switch (record.type) {
      case 'note':
        return Icons.note;
      case 'task':
        return Icons.task_alt;
      case 'advice':
        return Icons.lightbulb;
      case 'document_request':
        return Icons.description;
      case 'timeline':
        return Icons.timeline;
      default:
        return Icons.note;
    }
  }

  Color get _color {
    switch (record.type) {
      case 'note':
        return Colors.blue;
      case 'task':
        return AppTheme.green;
      case 'advice':
        return Colors.purple;
      case 'document_request':
        return Colors.orange;
      case 'timeline':
        return AppTheme.navy;
      default:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: _color.withOpacity(0.1),
          child: Icon(_icon, color: _color),
        ),
        title: Text(record.title),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (record.description != null && record.description!.isNotEmpty)
              Padding(
                padding: const EdgeInsets.only(top: 4),
                child: Text(record.description!),
              ),
            const SizedBox(height: 4),
            Text(
              '${record.type.replaceAll('_', ' ')} • ${record.createdAt.toString().substring(0, 10)}',
              style: TextStyle(fontSize: 12, color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }
}
