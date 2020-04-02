package org.zipper.dtx.core.utils;

import org.zipper.common.exceptions.SysException;
import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.commons.PluginType;
import org.zipper.dtx.common.spi.errors.CommonError;
import org.zipper.dtx.common.spi.plugins.AbstractJobPlugin;
import org.zipper.dtx.common.spi.plugins.AbstractPlugin;
import org.zipper.dtx.common.spi.plugins.AbstractTaskPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LoadUtil {

    private static Configuration allConfig;
    private static final String pluginTypeNameFormat = "%s.%s";

    private enum ContainerType {
        Job("Job"), Task("Task");
        private String type;

        private ContainerType(String type) {
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
        allConfig = Configuration.from(plugin.toString());
    }

    private static Map<String, JarLoader> jarLoaderCenter = new HashMap<String, JarLoader>();


    public static synchronized JarLoader getJarLoader(PluginType pluginType,
                                                      String pluginName) {
        Configuration pluginConf = getPluginConf(pluginType, pluginName);

        JarLoader jarLoader = jarLoaderCenter.get(generatePluginKey(pluginType, pluginName));
        if (null == jarLoader) {
            String pluginPath = pluginConf.getString("path");
            log.info(System.getProperty("user.dir"));
            log.info(pluginPath);
            if (StringUtils.isBlank(pluginPath)) {
                throw SysException.newException(
                        CommonError.RUNTIME_ERROR,
                        String.format("%s插件[%s]路径非法!", pluginType, pluginName));
            }
            jarLoader = new JarLoader(new String[]{pluginPath});
            jarLoaderCenter.put(generatePluginKey(pluginType, pluginName),
                    jarLoader);
        }

        return jarLoader;
    }

    private static Configuration getPluginConf(PluginType pluginType,
                                               String pluginName) {
        Configuration pluginConf = allConfig
                .getConfiguration(generatePluginKey(pluginType, pluginName));

        if (null == pluginConf) {
            throw SysException.newException(
                    CommonError.PLUGIN_INSTALL_ERROR,
                    String.format("不能找到插件[%s]的配置.", pluginName));
        }

        return pluginConf;
    }

    private static String generatePluginKey(PluginType pluginType, String pluginName) {
        return String.format(pluginTypeNameFormat, pluginType.toString(), pluginName);
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
            throw SysException.newException(CommonError.RUNTIME_ERROR, e);
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
            throw SysException.newException(CommonError.RUNTIME_ERROR, e);

        }
    }

    private static synchronized Class<? extends AbstractPlugin> loadPluginClass(
            PluginType pluginType, String pluginName,
            ContainerType pluginRunType) {
        Configuration pluginConf = getPluginConf(pluginType, pluginName);
        JarLoader jarLoader = LoadUtil.getJarLoader(pluginType, pluginName);
        try {
            return (Class<? extends AbstractPlugin>) jarLoader
                    .loadClass(pluginConf.getString("class") + "$"
                            + pluginRunType.value());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw SysException.newException(CommonError.RUNTIME_ERROR, e);
        }
    }
}
