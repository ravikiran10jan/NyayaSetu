import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nyayasetu/models/advocate.dart';
import 'package:nyayasetu/models/user_profile.dart';
import 'package:nyayasetu/widgets/advocate_card.dart';
import 'package:nyayasetu/widgets/app_scaffold.dart';

void main() {
  group('AdvocateCard', () {
    testWidgets('renders advocate name, practice area, and rating', (tester) async {
      final advocate = Advocate(
        id: '1',
        name: 'Adv. Rajesh Kumar Sharma',
        email: 'rajesh@example.com',
        practiceArea: 'Family Law',
        rating: 4.8,
        yearsOfExperience: 15,
        pricePerMinute: 5.0,
        isAvailable: true,
        languages: ['Hindi', 'English'],
        isVerified: true,
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AdvocateCard(
              advocate: advocate,
              onChat: () {},
              onCall: () {},
            ),
          ),
        ),
      );

      expect(find.text('Adv. Rajesh Kumar Sharma'), findsOneWidget);
      expect(find.text('Family Law'), findsOneWidget);
      expect(find.text('Available'), findsOneWidget);
      expect(find.text('Chat'), findsOneWidget);
      expect(find.text('Call'), findsOneWidget);
    });

    testWidgets('shows language chips', (tester) async {
      final advocate = Advocate(
        id: '1',
        name: 'Test Advocate',
        email: 'test@example.com',
        practiceArea: 'Criminal Law',
        languages: ['Tamil', 'English', 'Hindi'],
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AdvocateCard(advocate: advocate),
          ),
        ),
      );

      expect(find.text('Tamil'), findsOneWidget);
      expect(find.text('English'), findsOneWidget);
      expect(find.text('Hindi'), findsOneWidget);
    });

    testWidgets('chat and call callbacks fire correctly', (tester) async {
      bool chatPressed = false;
      bool callPressed = false;

      final advocate = Advocate(
        id: '1',
        name: 'Test Advocate',
        email: 'test@example.com',
        practiceArea: 'Corporate Law',
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AdvocateCard(
              advocate: advocate,
              onChat: () => chatPressed = true,
              onCall: () => callPressed = true,
            ),
          ),
        ),
      );

      await tester.tap(find.text('Chat'));
      expect(chatPressed, isTrue);

      await tester.tap(find.text('Call'));
      expect(callPressed, isTrue);
    });
  });

  group('UserProfile', () {
    test('fromJson creates correct UserProfile', () {
      final json = {
        'uid': 'test-uid',
        'name': 'Test User',
        'email': 'test@example.com',
        'role': 'client',
        'isVerified': false,
        'languages': ['Hindi', 'English'],
      };

      final profile = UserProfile.fromJson(json);

      expect(profile.uid, 'test-uid');
      expect(profile.name, 'Test User');
      expect(profile.email, 'test@example.com');
      expect(profile.role, 'client');
      expect(profile.isVerified, isFalse);
      expect(profile.languages, ['Hindi', 'English']);
    });

    test('toJson produces correct map', () {
      final profile = UserProfile(
        uid: 'test-uid',
        name: 'Test User',
        email: 'test@example.com',
        role: 'advocate',
        isVerified: true,
        languages: ['Tamil', 'English'],
      );

      final json = profile.toJson();

      expect(json['uid'], 'test-uid');
      expect(json['name'], 'Test User');
      expect(json['email'], 'test@example.com');
      expect(json['role'], 'advocate');
      expect(json['isVerified'], isTrue);
      expect(json['languages'], ['Tamil', 'English']);
    });

    test('copyWith creates modified copy', () {
      final profile = UserProfile(
        uid: 'test-uid',
        name: 'Test User',
        email: 'test@example.com',
      );

      final modified = profile.copyWith(name: 'Updated Name');

      expect(modified.uid, 'test-uid');
      expect(modified.name, 'Updated Name');
      expect(modified.email, 'test@example.com');
    });
  });

  group('Advocate', () {
    test('initials returns correct value for two-word name', () {
      final advocate = Advocate(
        id: '1',
        name: 'Rajesh Sharma',
        email: 'r@example.com',
        practiceArea: 'Family Law',
      );
      expect(advocate.initials, 'RS');
    });

    test('initials returns correct value for single-word name', () {
      final advocate = Advocate(
        id: '1',
        name: 'Priya',
        email: 'p@example.com',
        practiceArea: 'Corporate Law',
      );
      expect(advocate.initials, 'P');
    });

    test('fromJson parses all fields correctly', () {
      final json = {
        'id': 'test-id',
        'name': 'Test Advocate',
        'email': 'test@advocate.com',
        'practiceArea': 'Criminal Law',
        'rating': 4.5,
        'yearsOfExperience': 10,
        'pricePerMinute': 3.5,
        'isAvailable': true,
        'languages': ['Hindi', 'English'],
        'barCouncilId': 'DL/2010/1234',
        'jurisdictionState': 'Delhi',
        'city': 'New Delhi',
        'isVerified': true,
      };

      final advocate = Advocate.fromJson(json);

      expect(advocate.id, 'test-id');
      expect(advocate.name, 'Test Advocate');
      expect(advocate.email, 'test@advocate.com');
      expect(advocate.practiceArea, 'Criminal Law');
      expect(advocate.rating, 4.5);
      expect(advocate.yearsOfExperience, 10);
      expect(advocate.pricePerMinute, 3.5);
      expect(advocate.isAvailable, isTrue);
      expect(advocate.languages, ['Hindi', 'English']);
      expect(advocate.barCouncilId, 'DL/2010/1234');
      expect(advocate.isVerified, isTrue);
    });
  });

  group('AppScaffold Navigation', () {
    testWidgets('displays all four bottom navigation tabs', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: AppScaffold(
            currentIndex: 0,
            title: 'Test',
            child: const Text('Content'),
          ),
        ),
      );

      expect(find.text('Home'), findsOneWidget);
      expect(find.text('Advocates'), findsOneWidget);
      expect(find.text('Consultations'), findsOneWidget);
      expect(find.text('Workspace'), findsOneWidget);
    });

    testWidgets('highlights correct tab based on currentIndex', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: AppScaffold(
            currentIndex: 1,
            title: 'Test',
            child: const Text('Content'),
          ),
        ),
      );

      final bottomNav = tester.widget<BottomNavigationBar>(
        find.byType(BottomNavigationBar),
      );
      expect(bottomNav.currentIndex, 1);
    });
  });
}
