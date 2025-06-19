package dev.thoq.module;

import java.util.*;

@SuppressWarnings("unchecked")
public class ModuleRepository {
    private static final ModuleRepository INSTANCE = new ModuleRepository();
    private final Map<String, Module> modules = new HashMap<>();

    public ModuleRepository() {
    }

    /**
     * Retrieves the singleton instance of the {@code ModuleRepository}.
     *
     * @return the singleton instance of {@code ModuleRepository}
     */
    public static ModuleRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a given module into the repository. The module's name is used
     * as the key in a case-insensitive manner to ensure uniqueness.
     *
     * @param module the module to register in the repository
     */
    public void registerModule(Module module) {
        modules.put(module.getName().toLowerCase(), module);
    }

    /**
     * Retrieves a module by its class type from the repository.
     *
     * @param moduleClass the class of the module to retrieve
     * @param <T>         the type of the module extending the {@link java.lang.Module} class
     * @return the module instance of the specified class type if found, or {@code null} if no such module exists
     */
    public <T extends Module> T getModule(Class<T> moduleClass) {
        return (T) modules.values()
                .stream()
                .filter(module -> module.getClass().equals(moduleClass))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a {@link java.lang.Module} instance by its name from the repository.
     * The name lookup is case-insensitive.
     *
     * @param name the name of the module to retrieve
     * @return the {@link java.lang.Module} instance with the specified name, or {@code null} if no module is found
     */
    public Module getModuleByName(String name) {
        return modules.get(name.toLowerCase());
    }

    /**
     * Retrieves a collection of all registered modules in the repository.
     * The returned collection is unmodifiable and represents the current state
     * of the modules managed by the repository.
     *
     * @return an unmodifiable collection of all {@link java.lang.Module} instances in the repository
     */
    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }

    /**
     * Retrieves a list of all modules that are currently enabled in the repository.
     *
     * @return a list of enabled {@link java.lang.Module} instances
     */
    public List<Module> getEnabledModules() {
        return modules.values().stream()
                .filter(Module::isEnabled)
                .toList();
    }
}
