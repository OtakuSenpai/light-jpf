/*
 *    Copyright 2017 Luke Sosnicki
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ljpf;

/**
 * Created by souzen on 25.03.2017.
 */
public class PluginException extends RuntimeException {

    private final String pluginId;

    public PluginException(final String pluginId, final String message) {
        super(message);
        this.pluginId = pluginId;
    }

    public PluginException(final String pluginId, final String message, final Throwable cause) {
        super(message, cause);
        this.pluginId = pluginId;
    }

    public String getPluginId() {
        return pluginId;
    }

    @Override
    public String getMessage() {
        return String.format("Plugin '%s': %s", pluginId, super.getMessage());
    }

}