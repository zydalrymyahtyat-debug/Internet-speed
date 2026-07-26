import 'package:flutter/material.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _startOnBoot = true;
  bool _hideOnZero = false;
  String _unit = 'KB/s';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
      ),
      body: ListView(
        children: [
          SwitchListTile(
            title: const Text('Start on Boot'),
            subtitle: const Text('Automatically start monitoring when device boots up'),
            value: _startOnBoot,
            onChanged: (bool value) {
              setState(() {
                _startOnBoot = value;
              });
              // Save setting to shared preferences (to be implemented)
            },
          ),
          SwitchListTile(
            title: const Text('Hide when zero'),
            subtitle: const Text('Hide the notification when speed is 0 KB/s'),
            value: _hideOnZero,
            onChanged: (bool value) {
              setState(() {
                _hideOnZero = value;
              });
              // Save setting to shared preferences (to be implemented)
            },
          ),
          ListTile(
            title: const Text('Speed Unit'),
            subtitle: Text(_unit),
            trailing: const Icon(Icons.arrow_forward_ios, size: 16),
            onTap: () {
              // Show unit selection dialog
              _showUnitDialog();
            },
          ),
        ],
      ),
    );
  }

  void _showUnitDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Select Unit'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ListTile(
                title: const Text('Bytes/s (B/s)'),
                onTap: () {
                  setState(() { _unit = 'B/s'; });
                  Navigator.pop(context);
                },
              ),
              ListTile(
                title: const Text('Kilobytes/s (KB/s)'),
                onTap: () {
                  setState(() { _unit = 'KB/s'; });
                  Navigator.pop(context);
                },
              ),
              ListTile(
                title: const Text('Megabytes/s (MB/s)'),
                onTap: () {
                  setState(() { _unit = 'MB/s'; });
                  Navigator.pop(context);
                },
              ),
            ],
          ),
        );
      },
    );
  }
}
