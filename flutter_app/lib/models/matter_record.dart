class MatterRecord {
  final String id;
  final String matterId;
  final String type; // note, task, advice, document_request, timeline
  final String title;
  final String? description;
  final DateTime createdAt;
  final String createdBy;

  MatterRecord({
    required this.id,
    required this.matterId,
    required this.type,
    required this.title,
    this.description,
    required this.createdAt,
    required this.createdBy,
  });

  factory MatterRecord.fromJson(Map<String, dynamic> json) {
    return MatterRecord(
      id: json['id']?.toString() ?? '',
      matterId: json['matterId'] ?? '',
      type: json['type'] ?? 'note',
      title: json['title'] ?? '',
      description: json['description'],
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
      createdBy: json['createdBy'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'matterId': matterId,
      'type': type,
      'title': title,
      'description': description,
      'createdAt': createdAt.toIso8601String(),
      'createdBy': createdBy,
    };
  }
}
