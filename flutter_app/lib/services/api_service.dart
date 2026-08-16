import 'dart:convert';
import 'package:http/http.dart' as http;
import '../core/constants.dart';
import '../models/advocate.dart';
import '../models/booking.dart';
import '../models/legal_matter.dart';
import '../models/matter_record.dart';
import 'auth_service.dart';

class ApiService {
  final AuthService _authService;
  final String _baseUrl = AppConstants.apiBaseUrl;

  ApiService(this._authService);

  Future<Map<String, String>> _getHeaders() async {
    String? token = await _authService.getIdToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  // Advocates
  Future<List<Advocate>> getAdvocates({
    String? practiceArea,
    String? language,
  }) async {
    String url = '$_baseUrl/advocates';
    List<String> queryParams = [];
    if (practiceArea != null && practiceArea.isNotEmpty) {
      queryParams.add('practiceArea=$practiceArea');
    }
    if (language != null && language.isNotEmpty) {
      queryParams.add('language=$language');
    }
    if (queryParams.isNotEmpty) {
      url += '?${queryParams.join('&')}';
    }

    final response = await http.get(
      Uri.parse(url),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      List<dynamic> data = json.decode(response.body);
      return data.map((json) => Advocate.fromJson(json)).toList();
    }
    throw Exception('Failed to load advocates');
  }

  // Bookings
  Future<Booking> createBooking({
    required String advocateId,
    required String advocateName,
    required String clientId,
    required String mode,
    required DateTime scheduledStartsAt,
    String? clientNote,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/bookings'),
      headers: await _getHeaders(),
      body: json.encode({
        'advocateId': advocateId,
        'advocateName': advocateName,
        'clientId': clientId,
        'mode': mode,
        'scheduledStartsAt': scheduledStartsAt.toIso8601String(),
        'clientNote': clientNote,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return Booking.fromJson(json.decode(response.body));
    }
    throw Exception('Failed to create booking');
  }

  Future<List<Booking>> getMyBookings() async {
    final response = await http.get(
      Uri.parse('$_baseUrl/bookings/my'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      List<dynamic> data = json.decode(response.body);
      return data.map((json) => Booking.fromJson(json)).toList();
    }
    throw Exception('Failed to load bookings');
  }

  Future<Booking> updateBookingStatus({
    required String bookingId,
    required String status,
  }) async {
    final response = await http.patch(
      Uri.parse('$_baseUrl/bookings/$bookingId/status'),
      headers: await _getHeaders(),
      body: json.encode({'status': status}),
    );

    if (response.statusCode == 200) {
      return Booking.fromJson(json.decode(response.body));
    }
    throw Exception('Failed to update booking status');
  }

  // Matters
  Future<LegalMatter> createMatter({
    required String title,
    required String practiceArea,
    required String state,
    required String language,
    required String urgency,
    required String clientId,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/matters'),
      headers: await _getHeaders(),
      body: json.encode({
        'title': title,
        'practiceArea': practiceArea,
        'state': state,
        'language': language,
        'urgency': urgency,
        'clientId': clientId,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return LegalMatter.fromJson(json.decode(response.body));
    }
    throw Exception('Failed to create matter');
  }

  Future<List<LegalMatter>> getMatters() async {
    final response = await http.get(
      Uri.parse('$_baseUrl/matters/my'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      List<dynamic> data = json.decode(response.body);
      return data.map((json) => LegalMatter.fromJson(json)).toList();
    }
    throw Exception('Failed to load matters');
  }

  Future<LegalMatter> updateMatter({
    required String matterId,
    String? cnrNumber,
    bool? hasConflict,
  }) async {
    final response = await http.patch(
      Uri.parse('$_baseUrl/matters/$matterId'),
      headers: await _getHeaders(),
      body: json.encode({
        if (cnrNumber != null) 'cnrNumber': cnrNumber,
        if (hasConflict != null) 'hasConflict': hasConflict,
      }),
    );

    if (response.statusCode == 200) {
      return LegalMatter.fromJson(json.decode(response.body));
    }
    throw Exception('Failed to update matter');
  }

  // Matter Records
  Future<MatterRecord> createMatterRecord({
    required String matterId,
    required String type,
    required String title,
    String? description,
    required String createdBy,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/matters/$matterId/records'),
      headers: await _getHeaders(),
      body: json.encode({
        'matterId': matterId,
        'type': type,
        'title': title,
        'description': description,
        'createdBy': createdBy,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return MatterRecord.fromJson(json.decode(response.body));
    }
    throw Exception('Failed to create matter record');
  }

  Future<List<MatterRecord>> getMatterRecords(String matterId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/matters/$matterId/records'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      List<dynamic> data = json.decode(response.body);
      return data.map((json) => MatterRecord.fromJson(json)).toList();
    }
    throw Exception('Failed to load matter records');
  }
}
