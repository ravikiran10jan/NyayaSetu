class AnonymousChatMessage {
  final String id;
  final String sessionId;
  final String alias;
  final String mobile;
  final String content;
  final bool isFromUser;
  final DateTime timestamp;

  AnonymousChatMessage({
    required this.id,
    required this.sessionId,
    required this.alias,
    required this.mobile,
    required this.content,
    required this.isFromUser,
    required this.timestamp,
  });

  factory AnonymousChatMessage.fromJson(Map<String, dynamic> json) {
    return AnonymousChatMessage(
      id: json['id']?.toString() ?? '',
      sessionId: json['sessionId'] ?? '',
      alias: json['alias'] ?? '',
      mobile: json['mobile'] ?? '',
      content: json['content'] ?? '',
      isFromUser: json['isFromUser'] ?? false,
      timestamp: DateTime.tryParse(json['timestamp'] ?? '') ?? DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'sessionId': sessionId,
      'alias': alias,
      'mobile': mobile,
      'content': content,
      'isFromUser': isFromUser,
      'timestamp': timestamp.toIso8601String(),
    };
  }
}
