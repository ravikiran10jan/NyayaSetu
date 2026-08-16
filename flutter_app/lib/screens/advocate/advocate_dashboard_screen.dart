import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../core/theme.dart';
import '../../providers/bookings_provider.dart';
import '../../widgets/booking_status_chip.dart';

class AdvocateDashboardScreen extends StatefulWidget {
  const AdvocateDashboardScreen({super.key});

  @override
  State<AdvocateDashboardScreen> createState() => _AdvocateDashboardScreenState();
}

class _AdvocateDashboardScreenState extends State<AdvocateDashboardScreen> {
  String? _statusFilter;

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

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Dashboard'),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.person),
            onPressed: () => Navigator.pushNamed(context, '/advocate/profile'),
          ),
        ],
      ),
      body: Column(
        children: [
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                const Text(
                  'Filter by Status',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 8),
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      _buildStatusFilter('All', null),
                      _buildStatusFilter('Requested', 'requested'),
                      _buildStatusFilter('Accepted', 'accepted'),
                      _buildStatusFilter('Completed', 'completed'),
                      _buildStatusFilter('Cancelled', 'cancelled'),
                    ],
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: RefreshIndicator(
              onRefresh: _refresh,
              child: _buildBookingsList(provider),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatusFilter(String label, String? status) {
    final isSelected = _statusFilter == status;
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: FilterChip(
        label: Text(label),
        selected: isSelected,
        onSelected: (selected) {
          setState(() {
            _statusFilter = selected ? status : null;
          });
        },
        selectedColor: AppTheme.saffron.withOpacity(0.2),
        checkmarkColor: AppTheme.saffron,
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

    var bookings = provider.bookings;
    if (_statusFilter != null) {
      bookings = bookings.where((b) => b.status == _statusFilter).toList();
    }

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
                  'No bookings found',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
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
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Client: ${booking.clientId}',
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
                if (booking.status == 'requested') ...[
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: OutlinedButton(
                          onPressed: () => _updateBookingStatus(booking.id, 'cancelled'),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: Colors.red,
                          ),
                          child: const Text('Decline'),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () => _updateBookingStatus(booking.id, 'accepted'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: AppTheme.green,
                          ),
                          child: const Text('Accept'),
                        ),
                      ),
                    ],
                  ),
                ],
                if (booking.status == 'accepted') ...[
                  const SizedBox(height: 16),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: () => _updateBookingStatus(booking.id, 'completed'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppTheme.green,
                      ),
                      child: const Text('Mark as Completed'),
                    ),
                  ),
                ],
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _updateBookingStatus(String bookingId, String status) async {
    final provider = context.read<BookingsProvider>();
    try {
      await provider.updateBookingStatus(bookingId: bookingId, status: status);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Booking $status successfully'),
            backgroundColor: AppTheme.green,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }
}
