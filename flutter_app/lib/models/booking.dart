class Booking {
  final String id;
  final String advocateId;
  final String advocateName;
  final String clientId;
  final String mode; // 'chat' or 'call'
  final DateTime scheduledStartsAt;
  final String status; // requested, accepted, completed, cancelled
  final String? clientNote;
  final DateTime createdAt;

  Booking({
    required this.id,
    required this.advocateId,
    required this.advocateName,
    required this.clientId,
    required this.mode,
    required this.scheduledStartsAt,
    this.status = 'requested',
    this.clientNote,
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

  factory Booking.fromJson(Map<String, dynamic> json) {
    return Booking(
      id: json['id']?.toString() ?? '',
      advocateId: json['advocateId'] ?? json['lawyerId'] ?? '',
      advocateName: json['advocateName'] ?? json['practiceArea'] ?? 'Advocate',
      clientId: json['clientId'] ?? json['clientUserId'] ?? '',
      mode: json['mode'] ?? 'chat',
      scheduledStartsAt: _parseDate(json['scheduledStartsAt']),
      status: json['status'] ?? 'requested',
      clientNote: json['clientNote'],
      createdAt: _parseDate(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'advocateId': advocateId,
      'advocateName': advocateName,
      'clientId': clientId,
      'mode': mode,
      'scheduledStartsAt': scheduledStartsAt.toIso8601String(),
      'status': status,
      'clientNote': clientNote,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
