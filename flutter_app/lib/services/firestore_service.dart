import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/anonymous_chat.dart';
import '../models/user_profile.dart';

class FirestoreService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  // User Profile
  Stream<UserProfile> userProfileStream(String uid) {
    return _firestore
        .collection('users')
        .doc(uid)
        .snapshots()
        .map((doc) => UserProfile.fromJson(doc.data() as Map<String, dynamic>));
  }

  Future<void> updateUserProfile(UserProfile profile) async {
    await _firestore.collection('users').doc(profile.uid).update(profile.toJson());
  }

  // Anonymous Chat
  Stream<List<AnonymousChatMessage>> chatMessagesStream(String sessionId) {
    return _firestore
        .collection('anonymous_chats')
        .doc(sessionId)
        .collection('messages')
        .orderBy('timestamp', descending: false)
        .snapshots()
        .map((snapshot) => snapshot.docs
            .map((doc) => AnonymousChatMessage.fromJson(doc.data()))
            .toList());
  }

  Future<void> sendChatMessage(AnonymousChatMessage message) async {
    await _firestore
        .collection('anonymous_chats')
        .doc(message.sessionId)
        .collection('messages')
        .add(message.toJson());
  }
}
