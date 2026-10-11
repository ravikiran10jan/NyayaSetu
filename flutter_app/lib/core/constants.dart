class AppConstants {
  static const String appName = 'NyayaSetu';
  static const String tagline = 'Nyay ka Setu — The Bridge to Justice';

  // Backend URL (Cloud Run)
  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'https://nyayasetu-backend-547102415125.us-central1.run.app/api',
  );

  static const List<String> practiceAreas = [
    'Family Law',
    'Property Law',
    'Criminal Law',
    'Corporate Law',
    'Employment Law',
    'Consumer Law',
  ];

  static const List<String> indianStates = [
    'Andhra Pradesh',
    'Arunachal Pradesh',
    'Assam',
    'Bihar',
    'Chhattisgarh',
    'Goa',
    'Gujarat',
    'Haryana',
    'Himachal Pradesh',
    'Jharkhand',
    'Karnataka',
    'Kerala',
    'Madhya Pradesh',
    'Maharashtra',
    'Manipur',
    'Meghalaya',
    'Mizoram',
    'Nagaland',
    'Odisha',
    'Punjab',
    'Rajasthan',
    'Sikkim',
    'Tamil Nadu',
    'Telangana',
    'Tripura',
    'Uttar Pradesh',
    'Uttarakhand',
    'West Bengal',
    'Delhi',
  ];

  static const List<String> languages = [
    'Hindi',
    'English',
    'Tamil',
    'Telugu',
    'Kannada',
    'Malayalam',
    'Marathi',
    'Gujarati',
    'Bengali',
    'Punjabi',
    'Odia',
    'Urdu',
    'Assamese',
    'Maithili',
  ];
}
