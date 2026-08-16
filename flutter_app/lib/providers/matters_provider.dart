import 'package:flutter/material.dart';
import '../models/legal_matter.dart';
import '../models/matter_record.dart';
import '../services/api_service.dart';
import '../services/auth_service.dart';

class MattersProvider with ChangeNotifier {
  final ApiService _apiService = ApiService(AuthService());

  List<LegalMatter> _matters = [];
  Map<String, List<MatterRecord>> _records = {};
  bool _isLoading = false;
  String? _error;

  List<LegalMatter> get matters => _matters;
  Map<String, List<MatterRecord>> get records => _records;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadMatters() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _matters = await _apiService.getMatters();
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> createMatter({
    required String title,
    required String practiceArea,
    required String state,
    required String language,
    required String urgency,
    required String clientId,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _apiService.createMatter(
        title: title,
        practiceArea: practiceArea,
        state: state,
        language: language,
        urgency: urgency,
        clientId: clientId,
      );
      await loadMatters();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      rethrow;
    }
  }

  Future<void> updateMatter({
    required String matterId,
    String? cnrNumber,
    bool? hasConflict,
  }) async {
    try {
      await _apiService.updateMatter(
        matterId: matterId,
        cnrNumber: cnrNumber,
        hasConflict: hasConflict,
      );
      await loadMatters();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> loadRecords(String matterId) async {
    try {
      List<MatterRecord> records = await _apiService.getMatterRecords(matterId);
      _records[matterId] = records;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> addRecord({
    required String matterId,
    required String type,
    required String title,
    String? description,
    required String createdBy,
  }) async {
    try {
      await _apiService.createMatterRecord(
        matterId: matterId,
        type: type,
        title: title,
        description: description,
        createdBy: createdBy,
      );
      await loadRecords(matterId);
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      rethrow;
    }
  }
}
