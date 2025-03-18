import {Tabs} from 'expo-router';
import React from 'react';
import {Platform} from 'react-native';
import {Ionicons} from '@expo/vector-icons';

import {HapticTab} from '@/components/HapticTab';
import TabBarBackground from '@/components/ui/TabBarBackground';
import {Colors} from '@/constants/Colors';
import {useColorScheme} from '@/hooks/useColorScheme';

export default function TabLayout() {
    const colorScheme = useColorScheme();
    const tintColor = Colors[colorScheme ?? 'light'].tint;

    return (
        <Tabs
            screenOptions={{
                tabBarActiveTintColor: tintColor,
                headerShown: false,
                tabBarButton: HapticTab,
                tabBarBackground: TabBarBackground,
                tabBarLabelStyle: {
                    fontSize: 12,
                    fontWeight: '500',
                    marginBottom: 4,
                },
                tabBarIconStyle: {
                    marginTop: 4,
                },
                tabBarStyle: Platform.select({
                    ios: {
                        // Use a transparent background on iOS to show the blur effect
                        position: 'absolute',
                        height: 88,
                        paddingBottom: 24,
                    },
                    default: {
                        height: 64,
                        paddingBottom: 8,
                    },
                }),
            }}>
            <Tabs.Screen
                name="index"
                options={{
                    title: 'Home',
                    tabBarIcon: ({color}) => <Ionicons name="home" size={28} color={color}/>,
                }}
            />
            <Tabs.Screen
                name="modules"
                options={{
                    title: 'Modules',
                    tabBarIcon: ({color}) => <Ionicons name="grid" size={28} color={color}/>,
                }}
            />
            <Tabs.Screen
                name="configs"
                options={{
                    title: 'Configs',
                    tabBarIcon: ({color}) => <Ionicons name="settings-sharp" size={26} color={color}/>,
                }}
            />
        </Tabs>
    );
}
