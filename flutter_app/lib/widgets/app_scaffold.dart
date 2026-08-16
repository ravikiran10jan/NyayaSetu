import 'package:flutter/material.dart';
import '../core/theme.dart';

class AppScaffold extends StatelessWidget {
  final Widget child;
  final int currentIndex;
  final String title;

  const AppScaffold({
    super.key,
    required this.child,
    required this.currentIndex,
    this.title = 'NyayaSetu',
  });

  void _onItemTapped(BuildContext context, int index) {
    switch (index) {
      case 0:
        Navigator.pushNamedAndRemoveUntil(context, '/home', (route) => false);
        break;
      case 1:
        Navigator.pushNamedAndRemoveUntil(context, '/advocates', (route) => false);
        break;
      case 2:
        Navigator.pushNamedAndRemoveUntil(context, '/consultations', (route) => false);
        break;
      case 3:
        Navigator.pushNamedAndRemoveUntil(context, '/workspace', (route) => false);
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
      ),
      body: child,
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: currentIndex,
        onTap: (index) => _onItemTapped(context, index),
        type: BottomNavigationBarType.fixed,
        selectedItemColor: AppTheme.saffron,
        unselectedItemColor: Colors.grey,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.people),
            label: 'Advocates',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_today),
            label: 'Consultations',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.work),
            label: 'Workspace',
          ),
        ],
      ),
    );
  }
}
