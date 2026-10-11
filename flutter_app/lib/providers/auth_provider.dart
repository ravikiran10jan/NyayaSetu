import 'dart:async';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/user_profile.dart';
import '../services/auth_service.dart';

class AuthProvider with ChangeNotifier {
  final AuthService _authService = AuthService();
  StreamSubscription<User?>? _authSubscription;

  UserProfile? _user;
  bool _isLoading = false;
  bool _isInitialized = false;
  String? _error;

  UserProfile? get user => _user;
  bool get isLoading => _isLoading;
  bool get isInitialized => _isInitialized;
  String? get error => _error;
  bool get isAuthenticated => _user != null;
  String get userRole => _user?.role ?? 'client';

  /// Initialize auth state - checks if user is already logged in via Firebase
  void initAuth() {
    _authSubscription = _authService.authStateChanges.listen((user) async {
      if (user != null) {
        final profile = await _authService.getUserProfile();
        // Only update if we got a profile, or if we don't already have one.
        // This prevents overwriting a valid user set by signIn() when the
        // Firestore profile fetch temporarily fails or returns null.
        if (profile != null || _user == null) {
          _user = profile;
        }
      } else {
        _user = null;
      }
      _isInitialized = true;
      notifyListeners();
    });
  }

  Future<void> signUp({
    required String email,
    required String password,
    required String name,
    required String role,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final profile = await _authService.signUp(
        email: email,
        password: password,
        name: name,
        role: role,
      );
      if (profile != null) {
        _user = profile;
      } else {
        _user = await _authService.getUserProfile();
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> signIn({
    required String email,
    required String password,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final profile = await _authService.signIn(
        email: email,
        password: password,
      );
      if (profile != null) {
        _user = profile;
      } else {
        // Firebase Auth succeeded but no Firestore profile found.
        // Retry fetching the profile (auth listener may have loaded it).
        _user = await _authService.getUserProfile();
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> signInWithGoogle() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final profile = await _authService.signInWithGoogle();
      if (profile != null) {
        _user = profile;
      } else {
        _user = await _authService.getUserProfile();
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> signOut() async {
    await _authService.signOut();
    _user = null;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  @override
  void dispose() {
    _authSubscription?.cancel();
    super.dispose();
  }
}
