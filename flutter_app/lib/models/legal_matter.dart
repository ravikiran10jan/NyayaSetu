class LegalMatter {
  final String id;
  final String title;
  final String practiceArea;
  final String state;
  final String language;
  final String urgency; // low, medium, high, critical
  final String? cnrNumber;
  final String status; // active, resolved, archived
  final bool hasConflict;
  final String clientId;
  final DateTime createdAt;

  LegalMatter({
    required this.id,
    required this.title,
    required this.practiceArea,
    required this.state,
    required this.language,
    this.urgency = 'medium',
    this.cnrNumber,
    this.status = 'active',
    this.hasConflict = false,
    required this.clientId,
    required this.createdAt,
  });

  static DateTime _parseDate(dynamic v) {
    if (v is int) return DateTime.fromMillisecondsSinceEpoch(v);
    if (v is num) return DateTime.fromMillisecondsSinceEpoch(v.toInt());
    if (v is String && v.isNotEmpty) {
      final parsed = DateTime.tryParse(v);
      if (parsed != null) return parsed;
      final asInt = int.tryParse(v);
      if (asInt != null) return DateTime.fromMillisecondsSinceEpoch(asInt);
    }
    return DateTime.now();
  }

  factory LegalMatter.fromJson(Map<String, dynamic> json) {
    final conflictStatus = json['conflictStatus'];
    return LegalMatter(
      id: json['id']?.toString() ?? '',
      title: json['title'] ?? '',
      practiceArea: json['practiceArea'] ?? '',
      state: json['state'] ?? '',
      language: json['language'] ?? '',
      urgency: json['urgency'] ?? 'medium',
      cnrNumber: json['cnrNumber'],
      status: json['status'] ?? 'active',
      hasConflict: json['hasConflict'] == true ||
          (conflictStatus is String && conflictStatus.toLowerCase() != 'none' && conflictStatus.isNotEmpty),
      clientId: json['clientId'] ?? json['ownerUserId'] ?? '',
      createdAt: _parseDate(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'practiceArea': practiceArea,
      'state': state,
      'language': language,
      'urgency': urgency,
      'cnrNumber': cnrNumber,
      'status': status,
      'hasConflict': hasConflict,
      'clientId': clientId,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
