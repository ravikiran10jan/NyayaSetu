class Advocate {
  final String id;
  final String name;
  final String email;
  final String practiceArea;
  final double rating;
  final int yearsOfExperience;
  final double pricePerMinute;
  final bool isAvailable;
  final List<String> languages;
  final String? barCouncilId;
  final String? jurisdictionState;
  final String? city;
  final bool isVerified;

  Advocate({
    required this.id,
    required this.name,
    required this.email,
    required this.practiceArea,
    this.rating = 0.0,
    this.yearsOfExperience = 0,
    this.pricePerMinute = 0.0,
    this.isAvailable = true,
    this.languages = const [],
    this.barCouncilId,
    this.jurisdictionState,
    this.city,
    this.isVerified = false,
  });

  String get initials {
    final parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    }
    return name.isNotEmpty ? name[0].toUpperCase() : '?';
  }

  factory Advocate.fromJson(Map<String, dynamic> json) {
    return Advocate(
      id: json['id']?.toString() ?? '',
      name: json['name'] ?? '',
      email: json['email'] ?? '',
      practiceArea: json['practiceArea'] ?? '',
      rating: (json['rating'] ?? 0).toDouble(),
      yearsOfExperience: json['yearsOfExperience'] ?? 0,
      pricePerMinute: (json['pricePerMinute'] ?? 0).toDouble(),
      isAvailable: json['isAvailable'] ?? true,
      languages: (json['languages'] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
      barCouncilId: json['barCouncilId'],
      jurisdictionState: json['jurisdictionState'],
      city: json['city'],
      isVerified: json['isVerified'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'practiceArea': practiceArea,
      'rating': rating,
      'yearsOfExperience': yearsOfExperience,
      'pricePerMinute': pricePerMinute,
      'isAvailable': isAvailable,
      'languages': languages,
      'barCouncilId': barCouncilId,
      'jurisdictionState': jurisdictionState,
      'city': city,
      'isVerified': isVerified,
    };
  }
}
