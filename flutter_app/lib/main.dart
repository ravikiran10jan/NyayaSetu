import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'firebase_options.dart';
import 'providers/auth_provider.dart';
import 'providers/advocates_provider.dart';
import 'providers/bookings_provider.dart';
import 'providers/matters_provider.dart';
import 'core/theme.dart';
import 'screens/splash_screen.dart';
import 'screens/auth/login_screen.dart';
import 'screens/auth/register_screen.dart';
import 'screens/client/home_screen.dart';
import 'screens/client/advocates_screen.dart';
import 'screens/client/booking_screen.dart';
import 'screens/client/my_consultations_screen.dart';
import 'screens/client/workspace_screen.dart';
import 'screens/client/matter_detail_screen.dart';
import 'screens/anonymous/anonymous_chat_screen.dart';
import 'screens/advocate/advocate_dashboard_screen.dart';
import 'screens/advocate/advocate_profile_screen.dart';
import 'screens/lawyer_application_screen.dart';
import 'screens/privacy_center_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()..initAuth()),
        ChangeNotifierProvider(create: (_) => AdvocatesProvider()),
        ChangeNotifierProvider(create: (_) => BookingsProvider()),
        ChangeNotifierProvider(create: (_) => MattersProvider()),
      ],
      child: const MyApp(),
    ),
  );
}

GoRouter _createRouter(AuthProvider authProvider) {
  return GoRouter(
    initialLocation: '/',
    refreshListenable: authProvider,
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/home',
        builder: (context, state) => const HomeScreen(),
      ),
      GoRoute(
        path: '/advocates',
        builder: (context, state) => const AdvocatesScreen(),
      ),
      GoRoute(
        path: '/booking/:advocateId',
        builder: (context, state) {
          final advocateId = state.pathParameters['advocateId']!;
          final mode = state.uri.queryParameters['mode'] ?? 'chat';
          final advocateName = state.uri.queryParameters['name'] ?? 'Advocate';
          return BookingScreen(
            advocateId: advocateId,
            advocateName: advocateName,
            initialMode: mode,
          );
        },
      ),
      GoRoute(
        path: '/consultations',
        builder: (context, state) => const MyConsultationsScreen(),
      ),
      GoRoute(
        path: '/workspace',
        builder: (context, state) => const WorkspaceScreen(),
      ),
      GoRoute(
        path: '/matter/:matterId',
        builder: (context, state) {
          final matterId = state.pathParameters['matterId']!;
          return MatterDetailScreen(matterId: matterId);
        },
      ),
      GoRoute(
        path: '/anonymous',
        builder: (context, state) => const AnonymousChatScreen(),
      ),
      GoRoute(
        path: '/advocate/dashboard',
        builder: (context, state) => const AdvocateDashboardScreen(),
      ),
      GoRoute(
        path: '/advocate/profile',
        builder: (context, state) => const AdvocateProfileScreen(),
      ),
      GoRoute(
        path: '/lawyer-application',
        builder: (context, state) => const LawyerApplicationScreen(),
      ),
      GoRoute(
        path: '/privacy',
        builder: (context, state) => const PrivacyCenterScreen(),
      ),
    ],
    redirect: (context, state) {
      final authProvider = context.read<AuthProvider>();
      final isAuthenticated = authProvider.isAuthenticated;
      final currentPath = state.uri.path;

      // Public routes that don't require authentication
      final publicRoutes = ['/login', '/register', '/anonymous', '/lawyer-application'];

      if (publicRoutes.contains(currentPath)) {
        return null;
      }

      // If not authenticated and trying to access protected route, redirect to login
      if (!isAuthenticated && currentPath != '/') {
        return '/login';
      }

      // If authenticated and on login/register, redirect based on role
      if (isAuthenticated && (currentPath == '/login' || currentPath == '/register' || currentPath == '/')) {
        if (authProvider.userRole == 'advocate') {
          return '/advocate/dashboard';
        }
        return '/home';
      }

      return null;
    },
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = context.watch<AuthProvider>();
    final router = _createRouter(authProvider);

    return MaterialApp.router(
      title: 'NyayaSetu',
      theme: AppTheme.lightTheme,
      routerConfig: router,
      debugShowCheckedModeBanner: false,
    );
  }
}
