import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme.dart';
import '../../core/constants.dart';
import '../../providers/auth_provider.dart';
import '../../widgets/primary_button.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;

class LawyerApplicationScreen extends StatefulWidget {
  const LawyerApplicationScreen({super.key});

  @override
  State<LawyerApplicationScreen> createState() => _LawyerApplicationScreenState();
}

class _LawyerApplicationScreenState extends State<LawyerApplicationScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _barCouncilController = TextEditingController();
  final _enrolmentController = TextEditingController();
  final _cityController = TextEditingController();
  
  String? _selectedState;
  List<String> _selectedPracticeAreas = [];
  List<String> _selectedLanguages = [];
  bool _isSubmitting = false;

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _barCouncilController.dispose();
    _enrolmentController.dispose();
    _cityController.dispose();
    super.dispose();
  }

  Future<void> _submitApplication() async {
    if (!_formKey.currentState!.validate()) return;
    
    if (_selectedState == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a jurisdiction state')),
      );
      return;
    }
    
    if (_selectedPracticeAreas.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select at least one practice area')),
      );
      return;
    }
    
    if (_selectedLanguages.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select at least one language')),
      );
      return;
    }

    setState(() => _isSubmitting = true);

    try {
      final response = await http.post(
        Uri.parse('${AppConstants.apiBaseUrl}/lawyer-applications'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name': _nameController.text,
          'email': _emailController.text,
          'barCouncil': _barCouncilController.text,
          'enrolmentNumber': _enrolmentController.text,
          'jurisdictionState': _selectedState,
          'city': _cityController.text,
          'practiceAreas': _selectedPracticeAreas,
          'languages': _selectedLanguages,
        }),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Application submitted successfully!'),
              backgroundColor: AppTheme.green,
            ),
          );
          Navigator.pop(context);
        }
      } else {
        throw Exception('Failed to submit application');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isSubmitting = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Lawyer Application'),
        backgroundColor: AppTheme.saffron,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const SizedBox(height: 20),
              const Icon(
                Icons.gavel,
                size: 64,
                color: AppTheme.navy,
              ),
              const SizedBox(height: 16),
              const Text(
                'Join as an Advocate',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              Text(
                'Fill in your details to apply as a verified advocate on NyayaSetu',
                style: TextStyle(color: Colors.grey[600]),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 32),
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(
                  labelText: 'Full Name',
                  prefixIcon: Icon(Icons.person),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Please enter your name';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _emailController,
                keyboardType: TextInputType.emailAddress,
                decoration: const InputDecoration(
                  labelText: 'Email',
                  prefixIcon: Icon(Icons.email),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Please enter your email';
                  }
                  if (!value.contains('@')) {
                    return 'Please enter a valid email';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _barCouncilController,
                decoration: const InputDecoration(
                  labelText: 'Bar Council',
                  prefixIcon: Icon(Icons.business),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Please enter your bar council';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _enrolmentController,
                decoration: const InputDecoration(
                  labelText: 'Enrolment Number',
                  prefixIcon: Icon(Icons.badge),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Please enter your enrolment number';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _selectedState,
                decoration: const InputDecoration(
                  labelText: 'Jurisdiction State',
                  prefixIcon: Icon(Icons.location_on),
                ),
                items: AppConstants.indianStates.map((state) {
                  return DropdownMenuItem(value: state, child: Text(state));
                }).toList(),
                onChanged: (value) {
                  setState(() => _selectedState = value);
                },
                validator: (value) {
                  if (value == null) {
                    return 'Please select a state';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _cityController,
                decoration: const InputDecoration(
                  labelText: 'City',
                  prefixIcon: Icon(Icons.location_city),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Please enter your city';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 24),
              const Text(
                'Practice Areas',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: AppConstants.practiceAreas.map((area) {
                  final isSelected = _selectedPracticeAreas.contains(area);
                  return FilterChip(
                    label: Text(area),
                    selected: isSelected,
                    onSelected: (selected) {
                      setState(() {
                        if (selected) {
                          _selectedPracticeAreas.add(area);
                        } else {
                          _selectedPracticeAreas.remove(area);
                        }
                      });
                    },
                    selectedColor: AppTheme.saffron.withOpacity(0.2),
                    checkmarkColor: AppTheme.saffron,
                  );
                }).toList(),
              ),
              const SizedBox(height: 24),
              const Text(
                'Languages',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: AppConstants.languages.map((language) {
                  final isSelected = _selectedLanguages.contains(language);
                  return FilterChip(
                    label: Text(language),
                    selected: isSelected,
                    onSelected: (selected) {
                      setState(() {
                        if (selected) {
                          _selectedLanguages.add(language);
                        } else {
                          _selectedLanguages.remove(language);
                        }
                      });
                    },
                    selectedColor: AppTheme.saffron.withOpacity(0.2),
                    checkmarkColor: AppTheme.saffron,
                  );
                }).toList(),
              ),
              const SizedBox(height: 32),
              PrimaryButton(
                text: 'Submit Application',
                isLoading: _isSubmitting,
                onPressed: _submitApplication,
              ),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }
}
