import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:internet_speed_meter/main.dart';

void main() {
  testWidgets('App basic layout test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const InternetSpeedMeterApp());

    // Verify that our app bar title exists.
    expect(find.text('Internet Speed Meter'), findsOneWidget);

    // Verify that the setting icon exists.
    expect(find.byIcon(Icons.settings), findsOneWidget);

    // Verify the placeholder speed is shown
    expect(find.text('Current Speed'), findsOneWidget);
    expect(find.text('0 KB/s'), findsOneWidget);
  });
}
