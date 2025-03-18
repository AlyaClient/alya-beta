import React, {useEffect, useState} from 'react';
import {StyleSheet, Switch, TouchableOpacity, ActivityIndicator, ScrollView, RefreshControl} from 'react-native';
import {getModules, toggleModule, Module, Setting, updateModuleSetting, setModuleKeybind} from '@/services/api';
import {ThemedText} from '@/components/ThemedText';
import {ThemedView} from '@/components/ThemedView';
import {IconSymbol} from '@/components/ui/IconSymbol';
import {Colors} from '@/constants/Colors';
import {useColorScheme} from '@/hooks/useColorScheme';
import {KeybindModal} from '@/components/KeybindModal';

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

export default function ModulesScreen() {
    const [modules, setModules] = useState<Module[]>([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [expandedModule, setExpandedModule] = useState<string | null>(null);
    const [keybindModalVisible, setKeybindModalVisible] = useState(false);
    const [currentModule, setCurrentModule] = useState<string>('');
    const [currentKeyCode, setCurrentKeyCode] = useState<number>(-1);
    const colors = useThemeColor();

    const fetchModules = async () => {
        try {
            const data = await getModules();
            setModules(data);
        } catch (error) {
            console.error('Error fetching modules:', error);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useEffect(() => {
        fetchModules();
    }, []);

    const handleRefresh = () => {
        setRefreshing(true);
        fetchModules();
    };

    const handleToggleModule = async (moduleName: string) => {
        try {
            await toggleModule(moduleName);
            fetchModules();
        } catch (error) {
            console.error('Error toggling module:', error);
        }
    };

    const handleUpdateSetting = async (moduleName: string, settingName: string, value: any) => {
        try {
            await updateModuleSetting(moduleName, settingName, value);
            fetchModules();
        } catch (error) {
            console.error('Error updating setting:', error);
        }
    };

    const renderSettingValue = (setting: Setting, moduleName: string) => {
        if ((setting.type === 'Integer' || setting.type === 'Float' || setting.type === 'Double') &&
            (setting.value === 0 || setting.value === 1)) {
            return (
                <Switch
                    value={setting.value === 1}
                    onValueChange={(value) => handleUpdateSetting(moduleName, setting.name, value ? 1 : 0)}
                    trackColor={{false: '#767577', true: colors.tint}}
                />
            );
        }

        switch (setting.type) {
            case 'Boolean':
                return (
                    <Switch
                        value={setting.value}
                        onValueChange={(value) => handleUpdateSetting(moduleName, setting.name, value)}
                        trackColor={{false: '#767577', true: colors.tint}}
                    />
                );
            case 'Integer':
            case 'Float':
            case 'Double':
                return (
                    <ThemedView style={styles.numberSetting}>
                        <TouchableOpacity
                            onPress={() => {
                                const newValue = Math.max(
                                    setting.minValue !== null ? setting.minValue : Number.MIN_SAFE_INTEGER,
                                    setting.value - 1
                                );
                                handleUpdateSetting(moduleName, setting.name, newValue);
                            }}
                            style={[styles.numberButton, {backgroundColor: colors.buttonBackground}]}
                        >
                            <ThemedText>-</ThemedText>
                        </TouchableOpacity>
                        <ThemedText>{setting.value}</ThemedText>
                        <TouchableOpacity
                            onPress={() => {
                                const newValue = Math.min(
                                    setting.maxValue !== null ? setting.maxValue : Number.MAX_SAFE_INTEGER,
                                    setting.value + 1
                                );
                                handleUpdateSetting(moduleName, setting.name, newValue);
                            }}
                            style={[styles.numberButton, {backgroundColor: colors.buttonBackground}]}
                        >
                            <ThemedText>+</ThemedText>
                        </TouchableOpacity>
                    </ThemedView>
                );
            default:
                return <ThemedText>{String(setting.value)}</ThemedText>;
        }
    };

    if (loading) {
        return (
            <ThemedView style={styles.loadingContainer}>
                <ActivityIndicator size="large" color={colors.tint}/>
                <ThemedText>Loading modules...</ThemedText>
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
                <ThemedText type="title">Modules</ThemedText>
                <ThemedText>Manage your modules and settings</ThemedText>
            </ThemedView>

            {modules.map((module) => (
                <ThemedView
                    key={module.name}
                    style={[
                        styles.moduleCard,
                        {backgroundColor: colors.card, borderColor: colors.cardBorder}
                    ]}
                >
                    <TouchableOpacity
                        style={styles.moduleHeader}
                        onPress={() => setExpandedModule(expandedModule === module.name ? null : module.name)}
                    >
                        <ThemedView style={styles.moduleInfo}>
                            <ThemedText type="subtitle">{module.name}</ThemedText>
                            <ThemedText>{module.description}</ThemedText>
                        </ThemedView>
                        <ThemedView style={styles.moduleControls}>
                            <Switch
                                value={module.enabled}
                                onValueChange={() => handleToggleModule(module.name)}
                                trackColor={{false: '#767577', true: colors.tint}}
                            />
                            <IconSymbol
                                name={expandedModule === module.name ? 'chevron.up' : 'chevron.down'}
                                size={20}
                                color={colors.text}
                            />
                        </ThemedView>
                    </TouchableOpacity>

                    {expandedModule === module.name && (
                        <ThemedView style={styles.settingsContainer}>
                            <ThemedView>
                                <ThemedText type="defaultSemiBold" style={styles.settingsHeader}>
                                    Keybind
                                </ThemedText>
                                <ThemedView
                                    style={[
                                        styles.settingItem,
                                        {borderBottomColor: colors.cardBorder}
                                    ]}
                                >
                                    <ThemedView style={styles.settingInfo}>
                                        <ThemedText type="defaultSemiBold">Keyboard Shortcut</ThemedText>
                                        <ThemedText>Press a key to set a shortcut for this module</ThemedText>
                                    </ThemedView>
                                    <TouchableOpacity
                                        style={[{backgroundColor: colors.buttonBackground}]}
                                        onPress={() => {
                                            setCurrentModule(module.name);
                                            setCurrentKeyCode(module.keybind);
                                            setKeybindModalVisible(true);
                                        }}
                                    >
                                        <ThemedText>
                                            {module.keybind === -1 ? 'None' :
                                                String.fromCharCode(module.keybind)}
                                        </ThemedText>
                                    </TouchableOpacity>
                                </ThemedView>
                            </ThemedView>

                            {module.settings.length > 0 && (
                                <>
                                    <ThemedText type="defaultSemiBold" style={styles.settingsHeader}>
                                        Settings
                                    </ThemedText>
                                    {module.settings.map((setting) => (
                                        <ThemedView
                                            key={setting.name}
                                            style={[
                                                styles.settingItem,
                                                {borderBottomColor: colors.cardBorder}
                                            ]}
                                        >
                                            <ThemedView style={styles.settingInfo}>
                                                <ThemedText type="defaultSemiBold">{setting.name}</ThemedText>
                                                <ThemedText>{setting.description}</ThemedText>
                                            </ThemedView>
                                            <ThemedView style={styles.settingControl}>
                                                {renderSettingValue(setting, module.name)}
                                            </ThemedView>
                                        </ThemedView>
                                    ))}
                                </>
                            )}
                        </ThemedView>
                    )}
                </ThemedView>
            ))}

            <KeybindModal
                visible={keybindModalVisible}
                onClose={() => setKeybindModalVisible(false)}
                currentKeyCode={currentKeyCode}
                onKeySelected={async (keyCode) => {
                    try {
                        await setModuleKeybind(currentModule, keyCode);
                        fetchModules();
                        fetchModules();
                    } catch (error) {
                        console.error('Error setting keybind:', error);
                    }
                }}
            />
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
    moduleCard: {
        borderRadius: 12,
        marginBottom: 16,
        padding: 16,
        shadowColor: '#000',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 2,
        borderWidth: StyleSheet.hairlineWidth,
    },
    moduleHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    moduleInfo: {
        flex: 1,
        gap: 4,
    },
    moduleControls: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
    },
    settingsContainer: {
        marginTop: 16,
        gap: 12,
    },
    settingsHeader: {
        marginBottom: 8,
    },
    settingItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 8,
        borderBottomWidth: StyleSheet.hairlineWidth,
    },
    settingInfo: {
        flex: 1,
        gap: 4,
    },
    settingControl: {
        minWidth: 60,
        alignItems: 'flex-end',
    },
    numberSetting: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
    },
    numberButton: {
        width: 28,
        height: 28,
        borderRadius: 14,
        justifyContent: 'center',
        alignItems: 'center',
    },
});
