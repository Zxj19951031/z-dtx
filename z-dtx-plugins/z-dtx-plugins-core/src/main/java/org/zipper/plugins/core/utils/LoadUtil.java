package org.zipper.plugins.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.commons.PluginType;
import org.zipper.plugins.common.errors.CommonError;
import org.zipper.plugins.common.plugins.AbstractJobPlugin;
import org.zipper.plugins.common.plugins.AbstractPlugin;
import org.zipper.plugins.common.plugins.AbstractTaskPlugin;
import org.zipper.plugins.core.utils.JarLoader;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LoadUtil {


    private static final Logger log = LoggerFactory.getLogger(LoadUtil.class);
    private static JsonObject allConfig;
    private static final String PLUGIN_TYPE_NAME_FORMAT = "%s.%s";

    private enum ContainerType {
        Job("Job"), Task("Task");
        private final String type;

        ContainerType(String type) {
            this.type = type;
        }

        public String value() {
            return type;
        }
    }

    public static void bind() throws IOException {
        InputStream is = LoadUtil.class.getResourceAsStream("/plugin.json");
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder plugin = new StringBuilder();
        while ((line = bf.readLine()) != null) {
            plugin.append(line);
        }
        allConfig = JsonObject.from(plugin.toString());
    }

    private static final Map<String, JarLoader> jarLoaderCenter = new HashMap<String, JarLoader>();


    public static synchronized JarLoader getJarLoader(PluginType pluginType,
                                                      String pluginName) {
        JsonObject pluginConf = getPluginConf(pluginType, pluginName);

        JarLoader jarLoader = jarLoaderCenter.get(generatePluginKey(pluginType, pluginName));
        if (null == jarLoader) {
            String pluginPath = pluginConf.getString("path");
            log.info("当前工作目录{}", System.getProperty("user.dir"));
            log.info("期望插件所在目录{}/{}", System.getProperty("user.dir"), pluginPath);
            if (StringUtils.isBlank(pluginPath)) {
                throw HelperException.newException(
                        CommonError.RUNTIME_ERROR,
                        String.format("%s插件[%s]路径非法!", pluginType, pluginName));
            }
            jarLoader = new JarLoader(new String[]{pluginPath});
            jarLoaderCenter.put(generatePluginKey(pluginType, pluginName),
                    jarLoader);
        }

        return jarLoader;
    }

    private static JsonObject getPluginConf(PluginType pluginType,
                                            String pluginName) {
        JsonObject pluginConf = allConfig
                .getJsonObject(generatePluginKey(pluginType, pluginName));

        if (null == pluginConf) {
            throw HelperException.newException(
                    CommonError.PLUGIN_INSTALL_ERROR,
                    String.format("不能找到插件[%s]的配置.", pluginName));
        }

        return pluginConf;
    }

    private static String generatePluginKey(PluginType pluginType, String pluginName) {
        return String.format(PLUGIN_TYPE_NAME_FORMAT, pluginType.toString(), pluginName);
    }

    public static AbstractJobPlugin loadJobPlugin(PluginType pluginType,
                                                  String pluginName) {
        Class<? extends AbstractPlugin> clazz = LoadUtil.loadPluginClass(
                pluginType, pluginName, ContainerType.Job);
        try {
            AbstractJobPlugin jobPlugin = (AbstractJobPlugin) clazz.newInstance();
            jobPlugin.setPluginConf(getPluginConf(pluginType, pluginName));
            return jobPlugin;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HelperException.newException(CommonError.RUNTIME_ERROR, e);
        }
    }

    /**
     * 加载taskPlugin，reader、writer都可能加载
     *
     * @param pluginType
     * @param pluginName
     * @return
     */
    public static AbstractTaskPlugin loadTaskPlugin(PluginType pluginType,
                                                    String pluginName) {
        Class<? extends AbstractPlugin> clazz = LoadUtil.loadPluginClass(
                pluginType, pluginName, ContainerType.Task);

        try {
            AbstractTaskPlugin taskPlugin = (AbstractTaskPlugin) clazz
                    .newInstance();
            taskPlugin.setPluginConf(getPluginConf(pluginType, pluginName));
            return taskPlugin;
        } catch (Exception e) {
            throw HelperException.newException(CommonError.RUNTIME_ERROR, e);

        }
    }

    private static synchronized Class<? extends AbstractPlugin> loadPluginClass(
            PluginType pluginType, String pluginName,
            ContainerType pluginRunType) {
        JsonObject pluginConf = getPluginConf(pluginType, pluginName);
        JarLoader jarLoader = LoadUtil.getJarLoader(pluginType, pluginName);
        try {
            return (Class<? extends AbstractPlugin>) jarLoader
                    .loadClass(pluginConf.getString("class") + "$"
                            + pluginRunType.value());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HelperException.newException(CommonError.RUNTIME_ERROR, e);
        }
    }
}
