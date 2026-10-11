import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:google_sign_in/google_sign_in.dart';
import '../models/user_profile.dart';

class AuthService {
  final FirebaseAuth _auth = FirebaseAuth.instance;
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final GoogleSignIn _googleSignIn = GoogleSignIn();

  Stream<User?> get authStateChanges => _auth.authStateChanges();

  User? get currentUser => _auth.currentUser;

  Future<UserProfile?> signUp({
    required String email,
    required String password,
    required String name,
    required String role,
  }) async {
    try {
      UserCredential credential = await _auth.createUserWithEmailAndPassword(
        email: email,
        password: password,
      );

      if (credential.user != null) {
        UserProfile profile = UserProfile(
          uid: credential.user!.uid,
          name: name,
          email: email,
          role: role,
        );

        await _firestore.collection('users').doc(credential.user!.uid).set(profile.toJson());

        return profile;
      }
      return null;
    } catch (e) {
      rethrow;
    }
  }

  Future<UserProfile?> signIn({
    required String email,
    required String password,
  }) async {
    try {
      UserCredential credential = await _auth.signInWithEmailAndPassword(
        email: email,
        password: password,
      );

      if (credential.user != null) {
        DocumentSnapshot doc = await _firestore.collection('users').doc(credential.user!.uid).get();

        if (doc.exists) {
          return UserProfile.fromJson(doc.data() as Map<String, dynamic>);
        }
      }
      return null;
    } catch (e) {
      rethrow;
    }
  }

  Future<UserProfile?> signInWithGoogle() async {
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        return null; // User cancelled the sign-in
      }

      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      final OAuthCredential credential = GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );

      UserCredential userCredential = await _auth.signInWithCredential(credential);

      if (userCredential.user != null) {
        // Check if user profile exists in Firestore
        DocumentSnapshot doc = await _firestore.collection('users').doc(userCredential.user!.uid).get();

        if (doc.exists) {
          return UserProfile.fromJson(doc.data() as Map<String, dynamic>);
        } else {
          // Create a new user profile for Google Sign-In users (default role: client)
          UserProfile profile = UserProfile(
            uid: userCredential.user!.uid,
            name: userCredential.user!.displayName ?? googleUser.displayName ?? 'User',
            email: userCredential.user!.email ?? googleUser.email,
            role: 'client',
          );
          await _firestore.collection('users').doc(userCredential.user!.uid).set(profile.toJson());
          return profile;
        }
      }
      return null;
    } catch (e) {
      rethrow;
    }
  }

  Future<void> signOut() async {
    await _googleSignIn.signOut();
    await _auth.signOut();
  }

  Future<String?> getIdToken() async {
    User? user = _auth.currentUser;
    if (user != null) {
      return await user.getIdToken();
    }
    return null;
  }

  Future<UserProfile?> getUserProfile() async {
    User? user = _auth.currentUser;
    if (user != null) {
      DocumentSnapshot doc = await _firestore.collection('users').doc(user.uid).get();
      if (doc.exists) {
        return UserProfile.fromJson(doc.data() as Map<String, dynamic>);
      }
    }
    return null;
  }
}
