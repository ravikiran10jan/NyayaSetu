import '../models/anonymous_chat.dart';
import '../models/user_profile.dart';

// Stub implementation - Firebase will be configured later
class FirestoreService {
  Stream<UserProfile> userProfileStream(String uid) {
    return const Stream.empty();
  }

  Future<void> updateUserProfile(UserProfile profile) async {}

  Stream<List<AnonymousChatMessage>> chatMessagesStream(String sessionId) {
    return Stream.value([]);
  }

  Future<void> sendChatMessage(AnonymousChatMessage message) async {}
}
