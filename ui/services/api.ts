import axios from 'axios';

const API_BASE_URL = 'http://localhost:9595/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface Module {
  name: string;
  description: string;
  enabled: boolean;
  keybind: number;
  settings: Setting[];
}

export interface Setting {
  name: string;
  description: string;
  value: any;
  defaultValue: any;
  minValue: any;
  maxValue: any;
  type: string;
}

export const getModules = async (): Promise<Module[]> => {
  const response = await api.get('/modules');
  return response.data;
};

export const getModule = async (name: string): Promise<Module> => {
  const response = await api.get(`/modules/${name}`);
  return response.data;
};

export const toggleModule = async (name: string): Promise<{ success: boolean; enabled: boolean }> => {
  const response = await api.post(`/modules/${name}/toggle`);
  return response.data;
};

export const setModuleEnabled = async (name: string, enabled: boolean): Promise<{ success: boolean; enabled: boolean }> => {
  const response = await api.post(`/modules/${name}/enabled`, { enabled });
  return response.data;
};

export const getModuleSettings = async (moduleName: string): Promise<Setting[]> => {
  const response = await api.get(`/modules/${moduleName}/settings`);
  return response.data;
};

export const updateModuleSetting = async (
  moduleName: string,
  settingName: string,
  value: any
): Promise<{ success: boolean; value: any }> => {
  const response = await api.post(`/modules/${moduleName}/settings/${settingName}`, { value });
  return response.data;
};

export const getModuleKeybind = async (moduleName: string): Promise<{ key: number }> => {
  const response = await api.get(`/modules/${moduleName}/keybind`);
  return response.data;
};

export const setModuleKeybind = async (
  moduleName: string,
  key: number
): Promise<{ success: boolean; key: number }> => {
  const response = await api.post(`/modules/${moduleName}/keybind`, { key });
  return response.data;
};

export const getConfigs = async (): Promise<string[]> => {
  const response = await api.get('/configs');
  return response.data;
};

export const saveConfig = async (name: string): Promise<{ success: boolean }> => {
  const response = await api.post(`/configs/${name}/save`);
  return response.data;
};

export const loadConfig = async (name: string): Promise<{ success: boolean }> => {
  const response = await api.post(`/configs/${name}/load`);
  return response.data;
};

export default api;
