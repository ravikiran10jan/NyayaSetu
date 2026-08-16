import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../core/theme.dart';
import '../../providers/bookings_provider.dart';
import '../../widgets/booking_status_chip.dart';
import '../../widgets/app_scaffold.dart';

class MyConsultationsScreen extends StatefulWidget {
  const MyConsultationsScreen({super.key});

  @override
  State<MyConsultationsScreen> createState() => _MyConsultationsScreenState();
}

class _MyConsultationsScreenState extends State<MyConsultationsScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<BookingsProvider>().loadBookings();
    });
  }

  Future<void> _refresh() async {
    await context.read<BookingsProvider>().loadBookings();
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<BookingsProvider>();

    return AppScaffold(
      currentIndex: 2,
      title: 'My Consultations',
      child: RefreshIndicator(
        onRefresh: _refresh,
        child: _buildBookingsList(provider),
      ),
    );
  }

  Widget _buildBookingsList(BookingsProvider provider) {
    if (provider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (provider.error != null) {
      return ListView(
        children: [
          const SizedBox(height: 100),
          Center(
            child: Column(
              children: [
                const Icon(Icons.error_outline, size: 48, color: Colors.red),
                const SizedBox(height: 16),
                Text('Error: ${provider.error}'),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () => provider.loadBookings(),
                  child: const Text('Retry'),
                ),
              ],
            ),
          ),
        ],
      );
    }

    final bookings = provider.bookings;

    if (bookings.isEmpty) {
      return ListView(
        children: [
          const SizedBox(height: 100),
          const Center(
            child: Column(
              children: [
                Icon(Icons.event_busy, size: 64, color: Colors.grey),
                SizedBox(height: 16),
                Text(
                  'No consultations yet',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 8),
                Text(
                  'Book a consultation with an advocate',
                  style: TextStyle(color: Colors.grey),
                ),
              ],
            ),
          ),
        ],
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: bookings.length,
      itemBuilder: (context, index) {
        final booking = bookings[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    CircleAvatar(
                      backgroundColor: AppTheme.saffron,
                      child: Text(
                        _getInitials(booking.advocateName),
                        style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            booking.advocateName,
                            style: const TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            DateFormat('EEE, MMM d, yyyy • h:mm a').format(booking.scheduledStartsAt),
                            style: TextStyle(color: Colors.grey[600], fontSize: 14),
                          ),
                        ],
                      ),
                    ),
                    BookingStatusChip(status: booking.status),
                  ],
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    Icon(
                      booking.mode == 'chat' ? Icons.chat : Icons.call,
                      size: 16,
                      color: AppTheme.saffron,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      booking.mode.toUpperCase(),
                      style: const TextStyle(fontWeight: FontWeight.w500),
                    ),
                  ],
                ),
                if (booking.clientNote != null && booking.clientNote!.isNotEmpty) ...[
                  const SizedBox(height: 8),
                  Text(
                    'Note: ${booking.clientNote}',
                    style: TextStyle(color: Colors.grey[700], fontSize: 14),
                  ),
                ],
              ],
            ),
          ),
        );
      },
    );
  }

  String _getInitials(String name) {
    final parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    }
    return name.isNotEmpty ? name[0].toUpperCase() : '?';
  }
}
