import 'package:flutter/material.dart';
import '../models/booking.dart';
import '../services/api_service.dart';
import '../services/auth_service.dart';

class BookingsProvider with ChangeNotifier {
  final ApiService _apiService = ApiService(AuthService());

  List<Booking> _bookings = [];
  bool _isLoading = false;
  String? _error;
  String? _statusFilter;

  List<Booking> get bookings => _bookings;
  bool get isLoading => _isLoading;
  String? get error => _error;
  String? get statusFilter => _statusFilter;

  List<Booking> get filteredBookings {
    if (_statusFilter == null || _statusFilter!.isEmpty) {
      return _bookings;
    }
    return _bookings.where((b) => b.status == _statusFilter).toList();
  }

  Future<void> loadBookings() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _bookings = await _apiService.getMyBookings();
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> createBooking({
    required String advocateId,
    required String advocateName,
    required String clientId,
    required String mode,
    required DateTime scheduledStartsAt,
    String? clientNote,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _apiService.createBooking(
        advocateId: advocateId,
        advocateName: advocateName,
        clientId: clientId,
        mode: mode,
        scheduledStartsAt: scheduledStartsAt,
        clientNote: clientNote,
      );
      await loadBookings();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      rethrow;
    }
  }

  Future<void> updateBookingStatus({
    required String bookingId,
    required String status,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _apiService.updateBookingStatus(
        bookingId: bookingId,
        status: status,
      );
      await loadBookings();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      rethrow;
    }
  }

  void setStatusFilter(String? status) {
    _statusFilter = status;
    notifyListeners();
  }
}
