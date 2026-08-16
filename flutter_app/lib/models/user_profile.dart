class UserProfile {
  final String uid;
  final String name;
  final String email;
  final String role; // client, advocate
  final bool isVerified;
  final String? phone;
  final String? practiceArea;
  final String? barCouncilId;
  final String? jurisdictionState;
  final String? city;
  final List<String> languages;

  UserProfile({
    required this.uid,
    required this.name,
    required this.email,
    this.role = 'client',
    this.isVerified = false,
    this.phone,
    this.practiceArea,
    this.barCouncilId,
    this.jurisdictionState,
    this.city,
    this.languages = const [],
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      uid: json['uid'] ?? '',
      name: json['name'] ?? '',
      email: json['email'] ?? '',
      role: json['role'] ?? 'client',
      isVerified: json['isVerified'] ?? false,
      phone: json['phone'],
      practiceArea: json['practiceArea'],
      barCouncilId: json['barCouncilId'],
      jurisdictionState: json['jurisdictionState'],
      city: json['city'],
      languages: (json['languages'] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'uid': uid,
      'name': name,
      'email': email,
      'role': role,
      'isVerified': isVerified,
      'phone': phone,
      'practiceArea': practiceArea,
      'barCouncilId': barCouncilId,
      'jurisdictionState': jurisdictionState,
      'city': city,
      'languages': languages,
    };
  }

  UserProfile copyWith({
    String? name,
    String? email,
    String? role,
    bool? isVerified,
    String? phone,
    String? practiceArea,
    String? barCouncilId,
    String? jurisdictionState,
    String? city,
    List<String>? languages,
  }) {
    return UserProfile(
      uid: uid,
      name: name ?? this.name,
      email: email ?? this.email,
      role: role ?? this.role,
      isVerified: isVerified ?? this.isVerified,
      phone: phone ?? this.phone,
      practiceArea: practiceArea ?? this.practiceArea,
      barCouncilId: barCouncilId ?? this.barCouncilId,
      jurisdictionState: jurisdictionState ?? this.jurisdictionState,
      city: city ?? this.city,
      languages: languages ?? this.languages,
    );
  }
}
