import React, {useEffect, useState} from 'react';
import {
    StyleSheet,
    TouchableOpacity,
    ActivityIndicator,
    ScrollView,
    RefreshControl,
    TextInput,
    Alert,
    useColorScheme as _useColorScheme
} from 'react-native';
import {getConfigs, saveConfig, loadConfig} from '@/services/api';
import {ThemedText} from '@/components/ThemedText';
import {ThemedView} from '@/components/ThemedView';
import {IconSymbol} from '@/components/ui/IconSymbol';
import {Colors} from '@/constants/Colors';
import {useColorScheme} from '@/hooks/useColorScheme';

const useThemeColor = () => {
  const colorScheme = useColorScheme();
  const theme = Colors[colorScheme ?? 'light'];

  return {
    text: theme.text,
    background: theme.background,
    tint: theme.tint,
    tabIconDefault: theme.tabIconDefault,
    border: theme.border,
    card: theme.background,
    cardBorder: theme.border,
    buttonBackground: theme.background,
  };
};

export default function ConfigsScreen() {
    const [configs, setConfigs] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [newConfigName, setNewConfigName] = useState('');
    const colors = useThemeColor();

    const fetchConfigs = async () => {
        try {
            const data = await getConfigs();
            setConfigs(data);
        } catch (error) {
            console.error('Error fetching configs:', error);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useEffect(() => {
        fetchConfigs();
    }, []);

    const handleRefresh = () => {
        setRefreshing(true);
        fetchConfigs();
    };

    const handleSaveConfig = async () => {
        if (!newConfigName.trim()) {
            Alert.alert('Error', 'Please enter a config name');
            return;
        }

        try {
            await saveConfig(newConfigName);
            setNewConfigName('');
            fetchConfigs();
            Alert.alert('Success', `Config "${newConfigName}" saved successfully`);
        } catch (error) {
            console.error('Error saving config:', error);
            Alert.alert('Error', 'Failed to save config');
        }
    };

    const handleLoadConfig = async (configName: string) => {
        try {
            await loadConfig(configName);
            Alert.alert('Success', `Config "${configName}" loaded successfully`);
        } catch (error) {
            console.error('Error loading config:', error);
            Alert.alert('Error', 'Failed to load config');
        }
    };

    if (loading) {
        return (
            <ThemedView style={styles.loadingContainer}>
                <ActivityIndicator size="large" color={colors.tint}/>
                <ThemedText>Loading configs...</ThemedText>
            </ThemedView>
        );
    }

    return (
        <ScrollView
            style={styles.container}
            refreshControl={
                <RefreshControl refreshing={refreshing} onRefresh={handleRefresh}/>
            }
        >
            <ThemedView style={styles.header}>
                <ThemedText type="title">Configurations</ThemedText>
                <ThemedText>Save and load module configurations</ThemedText>
            </ThemedView>

            <ThemedView 
                style={[
                    styles.saveConfigContainer,
                    { backgroundColor: colors.card, borderColor: colors.cardBorder }
                ]}
            >
                <ThemedText type="subtitle">Save Current Configuration</ThemedText>
                <ThemedView style={styles.saveConfigForm}>
                    <TextInput
                        style={[
                            styles.input,
                            {
                                color: colors.text,
                                borderColor: colors.border,
                                backgroundColor: colors.background
                            }
                        ]}
                        placeholder="Enter config name"
                        placeholderTextColor={colors.tabIconDefault}
                        value={newConfigName}
                        onChangeText={setNewConfigName}
                    />
                    <TouchableOpacity
                        style={[styles.button, {backgroundColor: colors.tint}]}
                        onPress={handleSaveConfig}
                    >
                        <ThemedText style={styles.buttonText}>Save</ThemedText>
                    </TouchableOpacity>
                </ThemedView>
            </ThemedView>

            <ThemedView 
                style={[
                    styles.configsListContainer,
                    { backgroundColor: colors.card, borderColor: colors.cardBorder }
                ]}
            >
                <ThemedText type="subtitle">Available Configurations</ThemedText>
                {configs.length === 0 ? (
                    <ThemedText style={styles.noConfigsText}>No saved configurations</ThemedText>
                ) : (
                    configs.map((config) => (
                        <ThemedView 
                            key={config} 
                            style={[
                                styles.configItem,
                                { borderBottomColor: colors.cardBorder }
                            ]}
                        >
                            <ThemedText type="defaultSemiBold">{config}</ThemedText>
                            <TouchableOpacity
                                style={[styles.loadButton, {backgroundColor: colors.tint}]}
                                onPress={() => handleLoadConfig(config)}
                            >
                                <ThemedText style={styles.buttonText}>Load</ThemedText>
                            </TouchableOpacity>
                        </ThemedView>
                    ))
                )}
            </ThemedView>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 16,
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        gap: 16,
    },
    header: {
        marginBottom: 24,
        gap: 8,
        backgroundColor: 'transparent',
    },
    saveConfigContainer: {
        marginBottom: 24,
        gap: 12,
        padding: 16,
        borderRadius: 12,
        shadowColor: '#000',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 2,
        borderWidth: 1,
    },
    saveConfigForm: {
        flexDirection: 'row',
        gap: 12,
        alignItems: 'center',
    },
    input: {
        flex: 1,
        height: 40,
        borderWidth: 1,
        borderRadius: 8,
        paddingHorizontal: 12,
    },
    button: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        borderRadius: 8,
        justifyContent: 'center',
        alignItems: 'center',
    },
    buttonText: {
        color: 'white',
        fontWeight: 'bold',
    },
    configsListContainer: {
        gap: 12,
        padding: 16,
        borderRadius: 12,
        shadowColor: '#000',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 2,
        borderWidth: 1,
    },
    configItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 12,
        borderBottomWidth: StyleSheet.hairlineWidth,
    },
    loadButton: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        borderRadius: 8,
    },
    noConfigsText: {
        fontStyle: 'italic',
        marginTop: 12,
    },
});
