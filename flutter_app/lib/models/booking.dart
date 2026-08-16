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

  factory Booking.fromJson(Map<String, dynamic> json) {
    return Booking(
      id: json['id']?.toString() ?? '',
      advocateId: json['advocateId'] ?? '',
      advocateName: json['advocateName'] ?? '',
      clientId: json['clientId'] ?? '',
      mode: json['mode'] ?? 'chat',
      scheduledStartsAt: DateTime.tryParse(json['scheduledStartsAt'] ?? '') ?? DateTime.now(),
      status: json['status'] ?? 'requested',
      clientNote: json['clientNote'],
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
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
