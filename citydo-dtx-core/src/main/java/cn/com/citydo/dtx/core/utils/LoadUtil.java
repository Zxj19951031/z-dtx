package cn.com.citydo.dtx.core.utils;

import cn.com.citydo.consts.exceptions.SysException;
import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.commons.PluginType;
import cn.com.citydo.dtx.common.spi.errors.CommonError;
import cn.com.citydo.dtx.common.spi.plugins.AbstractJobPlugin;
import cn.com.citydo.dtx.common.spi.plugins.AbstractPlugin;
import cn.com.citydo.dtx.common.spi.plugins.AbstractTaskPlugin;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LoadUtil {

    private static Configuration allConfig;
    private static final String pluginTypeNameFormat = "plugin.%s.%s";

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

    public static void bind() {
        allConfig = Configuration.from(FileUtil.readString("./plugin.json", "utf-8"));
    }

    private static Map<String, JarLoader> jarLoaderCenter = new HashMap<String, JarLoader>();


    public static synchronized JarLoader getJarLoader(PluginType pluginType,
                                                      String pluginName) {
        Configuration pluginConf = getPluginConf(pluginType, pluginName);

        JarLoader jarLoader = jarLoaderCenter.get(generatePluginKey(pluginType, pluginName));
        if (null == jarLoader) {
            String pluginPath = pluginConf.getString("path");
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
            throw SysException.newException(CommonError.RUNTIME_ERROR, e);
    }
    }
}
