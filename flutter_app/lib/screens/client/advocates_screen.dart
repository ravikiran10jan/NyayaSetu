import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../core/theme.dart';
import '../../core/constants.dart';
import '../../providers/advocates_provider.dart';
import '../../widgets/advocate_card.dart';
import '../../widgets/app_scaffold.dart';

class AdvocatesScreen extends StatefulWidget {
  const AdvocatesScreen({super.key});

  @override
  State<AdvocatesScreen> createState() => _AdvocatesScreenState();
}

class _AdvocatesScreenState extends State<AdvocatesScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<AdvocatesProvider>().loadAdvocates();
    });
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AdvocatesProvider>();

    return AppScaffold(
      currentIndex: 1,
      title: 'Find Advocates',
      child: Column(
        children: [
          _buildSearchAndFilters(provider),
          Expanded(
            child: _buildAdvocatesList(provider),
          ),
        ],
      ),
    );
  }

  Widget _buildSearchAndFilters(AdvocatesProvider provider) {
    return Container(
      color: Colors.white,
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          TextField(
            decoration: InputDecoration(
              hintText: 'Search advocates...',
              prefixIcon: const Icon(Icons.search),
              suffixIcon: provider.searchQuery.isNotEmpty
                  ? IconButton(
                      icon: const Icon(Icons.clear),
                      onPressed: () => provider.setSearchQuery(''),
                    )
                  : null,
            ),
            onChanged: provider.setSearchQuery,
          ),
          const SizedBox(height: 12),
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: [
                _buildFilterChip('All Areas', null, provider),
                ...AppConstants.practiceAreas.map((area) => _buildFilterChip(area, area, provider)),
              ],
            ),
          ),
          const SizedBox(height: 12),
          DropdownButtonFormField<String>(
            value: provider.selectedLanguage,
            decoration: const InputDecoration(
              labelText: 'Filter by Language',
              prefixIcon: Icon(Icons.language),
            ),
            items: [
              const DropdownMenuItem(value: null, child: Text('All Languages')),
              ...AppConstants.languages.map((lang) => DropdownMenuItem(
                    value: lang,
                    child: Text(lang),
                  )),
            ],
            onChanged: provider.setLanguage,
          ),
        ],
      ),
    );
  }

  Widget _buildFilterChip(String label, String? value, AdvocatesProvider provider) {
    final isSelected = provider.selectedPracticeArea == value;
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: FilterChip(
        label: Text(label),
        selected: isSelected,
        onSelected: (selected) {
          provider.setPracticeArea(selected ? value : null);
        },
        selectedColor: AppTheme.saffron.withOpacity(0.2),
        checkmarkColor: AppTheme.saffron,
      ),
    );
  }

  Widget _buildAdvocatesList(AdvocatesProvider provider) {
    if (provider.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (provider.error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 48, color: Colors.red),
            const SizedBox(height: 16),
            Text('Error: ${provider.error}'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => provider.loadAdvocates(),
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    final advocates = provider.filteredAdvocates;

    if (advocates.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.search_off, size: 48, color: Colors.grey),
            SizedBox(height: 16),
            Text('No advocates found'),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.only(top: 8, bottom: 16),
      itemCount: advocates.length,
      itemBuilder: (context, index) {
        final advocate = advocates[index];
        return AdvocateCard(
          advocate: advocate,
          onChat: () => context.go('/booking/${advocate.id}?mode=chat'),
          onCall: () => context.go('/booking/${advocate.id}?mode=call'),
        );
      },
    );
  }
}
