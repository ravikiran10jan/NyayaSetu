import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/advocate.dart';

class AdvocatesProvider with ChangeNotifier {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  List<Advocate> _advocates = [];
  bool _isLoading = false;
  String? _error;
  String? _selectedPracticeArea;
  String? _selectedLanguage;
  String _searchQuery = '';

  List<Advocate> get advocates => _advocates;
  bool get isLoading => _isLoading;
  String? get error => _error;
  String? get selectedPracticeArea => _selectedPracticeArea;
  String? get selectedLanguage => _selectedLanguage;
  String get searchQuery => _searchQuery;

  List<Advocate> get filteredAdvocates {
    List<Advocate> filtered = _advocates;

    if (_searchQuery.isNotEmpty) {
      filtered = filtered.where((advocate) {
        return advocate.name.toLowerCase().contains(_searchQuery.toLowerCase()) ||
               advocate.practiceArea.toLowerCase().contains(_searchQuery.toLowerCase());
      }).toList();
    }

    if (_selectedPracticeArea != null && _selectedPracticeArea!.isNotEmpty) {
      filtered = filtered.where((a) => a.practiceArea == _selectedPracticeArea).toList();
    }

    if (_selectedLanguage != null && _selectedLanguage!.isNotEmpty) {
      filtered = filtered.where((a) => a.languages.contains(_selectedLanguage)).toList();
    }

    return filtered;
  }

  Future<void> loadAdvocates() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final snapshot = await _firestore
          .collection('lawyers')
          .where('status', isEqualTo: 'active')
          .get();

      _advocates = snapshot.docs
          .map((doc) => Advocate.fromJson({
                ...doc.data(),
                'id': doc.id,
              }))
          .toList();
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void setPracticeArea(String? area) {
    _selectedPracticeArea = area;
    notifyListeners();
  }

  void setLanguage(String? language) {
    _selectedLanguage = language;
    notifyListeners();
  }

  void setSearchQuery(String query) {
    _searchQuery = query;
    notifyListeners();
  }
}
