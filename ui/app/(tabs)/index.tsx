import React from 'react';
import {StyleSheet, ScrollView, View, TouchableOpacity} from 'react-native';
import {ThemedText} from '@/components/ThemedText';
import {useRouter} from 'expo-router';
import {Ionicons} from '@expo/vector-icons';
import {useColorScheme} from '@/hooks/useColorScheme';
import {Colors} from '@/constants/Colors';
import {HelloWave} from "@/components/HelloWave";
import * as API from "@/services/api";

export default function Welcome() {
    const router = useRouter();
    const colorScheme = useColorScheme();
    const tintColor = Colors[colorScheme ?? 'light'].tint;
    const backgroundColor = Colors[colorScheme ?? 'light'].background;
    const cardBgColor = colorScheme === 'dark' ? '#1c1c1c' : '#ffffff';
    const textColor = Colors[colorScheme ?? 'light'].text;
    const [version, setVersion] = React.useState('v1.0');

    React.useEffect(() => {
        const fetchVersion = async () => {
            try {
                const versionData = await API.getVersion();
                setVersion(versionData);
            } catch (error) {
                console.error('Failed to fetch version:', error);
            }
        };

        fetchVersion().then();
    }, []);

    const navigateTo = (screen: any) => {
        router.push(screen as never);
    };

    return (
        <ScrollView
            style={[styles.container, {backgroundColor}]}
            contentContainerStyle={styles.contentContainer}
        >
            <View style={styles.header}>
                <ThemedText
                    style={styles.title}
                >
                    Welcome to Rhy WebUI
                    <HelloWave/>
                </ThemedText>
            </View>

            <View style={[styles.card, {backgroundColor: cardBgColor}]}>
                <View style={styles.cardHeader}>
                    <Ionicons name="notifications" size={24} color={tintColor}/>
                    <ThemedText style={styles.cardTitle}>Recent Updates</ThemedText>
                </View>
                <ThemedText style={styles.cardContent}>
                    • New module interface added{'\n'}
                    • Performance improvements{'\n'}
                    • Bug fixes for navigation system
                </ThemedText>
            </View>

            <View style={[styles.card, {backgroundColor: cardBgColor}]}>
                <View style={styles.cardHeader}>
                    <Ionicons name="star" size={24} color={tintColor}/>
                    <ThemedText style={styles.cardTitle}>Quick Access</ThemedText>
                </View>
                <View style={styles.quickAccessContainer}>
                    <TouchableOpacity
                        style={styles.quickAccessItem}
                        onPress={() => navigateTo('/modules')}
                        activeOpacity={0.7}
                    >
                        <Ionicons name="grid" size={32} color={tintColor}/>
                        <ThemedText style={styles.quickAccessText}>Modules</ThemedText>
                    </TouchableOpacity>

                    <TouchableOpacity
                        style={styles.quickAccessItem}
                        onPress={() => navigateTo('/configs')}
                        activeOpacity={0.7}
                    >
                        <Ionicons name="settings-sharp" size={32} color={tintColor}/>
                        <ThemedText style={styles.quickAccessText}>Configs</ThemedText>
                    </TouchableOpacity>
                </View>
            </View>

            <View style={styles.footer}>
                <ThemedText style={[styles.footerText, {color: textColor + '80'}]}>Version {version}</ThemedText>
            </View>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    contentContainer: {
        padding: 16,
        paddingBottom: 32,
    },
    header: {
        marginVertical: 24,
        alignItems: 'center',
    },
    title: {
        fontSize: 28,
        fontWeight: 'bold',
        marginBottom: 8,
    },
    subtitle: {
        fontSize: 16,
        opacity: 0.7,
        textAlign: 'center',
    },
    card: {
        borderRadius: 12,
        padding: 16,
        marginBottom: 16,
        shadowColor: '#000',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
    },
    cardHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 12,
    },
    cardTitle: {
        fontSize: 18,
        fontWeight: '600',
        marginLeft: 8,
    },
    cardContent: {
        fontSize: 15,
        lineHeight: 22,
    },
    quickAccessContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        marginTop: 8,
    },
    quickAccessItem: {
        alignItems: 'center',
        padding: 12,
        borderRadius: 8,
    },
    quickAccessText: {
        marginTop: 8,
        fontSize: 14,
    },
    footer: {
        alignItems: 'center',
        marginTop: 24,
    },
    footerText: {
        fontSize: 12,
    }
});
