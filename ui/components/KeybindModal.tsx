import React, { useEffect, useState } from 'react';
import { Modal, View, Text, StyleSheet, TouchableOpacity, Pressable } from 'react-native';
import { ThemedText } from './ThemedText';
import { ThemedView } from './ThemedView';
import { Colors } from '@/constants/Colors';
import { useColorScheme } from '@/hooks/useColorScheme';

// Mapping from JavaScript key codes to GLFW key codes
// This is a partial mapping of common keys
const keyCodeMap: Record<string, number> = {
  'Escape': 256, // GLFW_KEY_ESCAPE
  'F1': 290, // GLFW_KEY_F1
  'F2': 291,
  'F3': 292,
  'F4': 293,
  'F5': 294,
  'F6': 295,
  'F7': 296,
  'F8': 297,
  'F9': 298,
  'F10': 299,
  'F11': 300,
  'F12': 301,
  'PrintScreen': 283, // GLFW_KEY_PRINT_SCREEN
  'ScrollLock': 281, // GLFW_KEY_SCROLL_LOCK
  'Pause': 284, // GLFW_KEY_PAUSE
  'Insert': 260, // GLFW_KEY_INSERT
  'Home': 268, // GLFW_KEY_HOME
  'PageUp': 266, // GLFW_KEY_PAGE_UP
  'Delete': 261, // GLFW_KEY_DELETE
  'End': 269, // GLFW_KEY_END
  'PageDown': 267, // GLFW_KEY_PAGE_DOWN
  'ArrowRight': 262, // GLFW_KEY_RIGHT
  'ArrowLeft': 263, // GLFW_KEY_LEFT
  'ArrowDown': 264, // GLFW_KEY_DOWN
  'ArrowUp': 265, // GLFW_KEY_UP
  'NumLock': 282, // GLFW_KEY_NUM_LOCK
  'Backspace': 259, // GLFW_KEY_BACKSPACE
  'Tab': 258, // GLFW_KEY_TAB
  'Enter': 257, // GLFW_KEY_ENTER
  'ShiftLeft': 340, // GLFW_KEY_LEFT_SHIFT
  'ShiftRight': 344, // GLFW_KEY_RIGHT_SHIFT
  'ControlLeft': 341, // GLFW_KEY_LEFT_CONTROL
  'ControlRight': 345, // GLFW_KEY_RIGHT_CONTROL
  'AltLeft': 342, // GLFW_KEY_LEFT_ALT
  'AltRight': 346, // GLFW_KEY_RIGHT_ALT
  'MetaLeft': 343, // GLFW_KEY_LEFT_SUPER
  'MetaRight': 347, // GLFW_KEY_RIGHT_SUPER
  'Space': 32, // GLFW_KEY_SPACE
  'CapsLock': 280, // GLFW_KEY_CAPS_LOCK
  '`': 96, // GLFW_KEY_GRAVE_ACCENT
  '1': 49, // GLFW_KEY_1
  '2': 50,
  '3': 51,
  '4': 52,
  '5': 53,
  '6': 54,
  '7': 55,
  '8': 56,
  '9': 57,
  '0': 48, // GLFW_KEY_0
  '-': 45, // GLFW_KEY_MINUS
  '=': 61, // GLFW_KEY_EQUAL
  '[': 91, // GLFW_KEY_LEFT_BRACKET
  ']': 93, // GLFW_KEY_RIGHT_BRACKET
  '\\': 92, // GLFW_KEY_BACKSLASH
  ';': 59, // GLFW_KEY_SEMICOLON
  '\'': 39, // GLFW_KEY_APOSTROPHE
  ',': 44, // GLFW_KEY_COMMA
  '.': 46, // GLFW_KEY_PERIOD
  '/': 47, // GLFW_KEY_SLASH
  'a': 65, // GLFW_KEY_A
  'b': 66,
  'c': 67,
  'd': 68,
  'e': 69,
  'f': 70,
  'g': 71,
  'h': 72,
  'i': 73,
  'j': 74,
  'k': 75,
  'l': 76,
  'm': 77,
  'n': 78,
  'o': 79,
  'p': 80,
  'q': 81,
  'r': 82,
  's': 83,
  't': 84,
  'u': 85,
  'v': 86,
  'w': 87,
  'x': 88,
  'y': 89,
  'z': 90, // GLFW_KEY_Z
  'A': 65, // GLFW_KEY_A (uppercase)
  'B': 66,
  'C': 67,
  'D': 68,
  'E': 69,
  'F': 70,
  'G': 71,
  'H': 72,
  'I': 73,
  'J': 74,
  'K': 75,
  'L': 76,
  'M': 77,
  'N': 78,
  'O': 79,
  'P': 80,
  'Q': 81,
  'R': 82,
  'S': 83,
  'T': 84,
  'U': 85,
  'V': 86,
  'W': 87,
  'X': 88,
  'Y': 89,
  'Z': 90, // GLFW_KEY_Z (uppercase)
};

// Function to get a human-readable key name from a GLFW key code
const getKeyName = (keyCode: number): string => {
  if (keyCode === -1) return 'None';
  
  // For letter keys (A-Z)
  if (keyCode >= 65 && keyCode <= 90) {
    return String.fromCharCode(keyCode);
  }
  
  // For number keys (0-9)
  if (keyCode >= 48 && keyCode <= 57) {
    return String.fromCharCode(keyCode);
  }
  
  // For other keys, use a lookup table
  const keyNames: Record<number, string> = {
    32: 'Space',
    256: 'Escape',
    257: 'Enter',
    258: 'Tab',
    259: 'Backspace',
    280: 'Caps Lock',
    340: 'Left Shift',
    341: 'Left Ctrl',
    342: 'Left Alt',
    343: 'Left Super',
    344: 'Right Shift',
    345: 'Right Ctrl',
    346: 'Right Alt',
    347: 'Right Super',
  };
  
  return keyNames[keyCode] || `Key ${keyCode}`;
};

interface KeybindModalProps {
  visible: boolean;
  onClose: () => void;
  onKeySelected: (keyCode: number) => void;
  currentKeyCode: number;
}

export const KeybindModal: React.FC<KeybindModalProps> = ({
  visible,
  onClose,
  onKeySelected,
  currentKeyCode,
}) => {
  const [keyCode, setKeyCode] = useState<number>(currentKeyCode);
  const [keyName, setKeyName] = useState<string>(getKeyName(currentKeyCode));
  const colorScheme = useColorScheme();
  const colors = Colors[colorScheme ?? 'light'];

  useEffect(() => {
    if (visible) {
      // Set up keyboard event listeners when the modal is visible
      const handleKeyDown = (e: KeyboardEvent) => {
        e.preventDefault();
        
        // Get the GLFW key code from our mapping
        const glfwKeyCode = keyCodeMap[e.key] || -1;
        
        if (glfwKeyCode !== -1) {
          setKeyCode(glfwKeyCode);
          setKeyName(getKeyName(glfwKeyCode));
        }
      };
      
      // Add event listener
      document.addEventListener('keydown', handleKeyDown);
      
      // Clean up
      return () => {
        document.removeEventListener('keydown', handleKeyDown);
      };
    }
  }, [visible]);

  const handleSave = () => {
    onKeySelected(keyCode);
    onClose();
  };

  const handleClear = () => {
    setKeyCode(-1);
    setKeyName('None');
  };

  return (
    <Modal
      animationType="fade"
      transparent={true}
      visible={visible}
      onRequestClose={onClose}
    >
      <Pressable style={styles.modalOverlay} onPress={onClose}>
        <Pressable style={[styles.modalContent, { backgroundColor: colors.background }]} onPress={e => e.stopPropagation()}>
          <ThemedText type="subtitle" style={styles.title}>Set Keybind</ThemedText>
          
          <ThemedView style={styles.keyDisplay}>
            <ThemedText type="defaultSemiBold">{keyName}</ThemedText>
          </ThemedView>
          
          <ThemedText style={styles.instructions}>
            Press any key to set as keybind
          </ThemedText>
          
          <ThemedView style={styles.buttonContainer}>
            <TouchableOpacity
              style={[styles.button, { backgroundColor: colors.tabIconDefault }]}
              onPress={handleClear}
            >
              <ThemedText style={styles.buttonText}>Clear</ThemedText>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={[styles.button, { backgroundColor: colors.tint }]}
              onPress={handleSave}
            >
              <ThemedText style={styles.buttonText}>Save</ThemedText>
            </TouchableOpacity>
          </ThemedView>
        </Pressable>
      </Pressable>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalOverlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContent: {
    width: '80%',
    padding: 20,
    borderRadius: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
  title: {
    marginBottom: 20,
  },
  keyDisplay: {
    width: '100%',
    height: 60,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: StyleSheet.hairlineWidth,
    borderRadius: 8,
    marginBottom: 20,
  },
  instructions: {
    marginBottom: 20,
    textAlign: 'center',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
  },
  button: {
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 8,
    minWidth: 100,
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontWeight: 'bold',
  },
});