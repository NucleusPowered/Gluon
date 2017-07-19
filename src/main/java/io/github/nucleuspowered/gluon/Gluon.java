/*
 * Copyright (c) 2017 Daniel Naylor (dualspiral), Nucleus Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.github.nucleuspowered.gluon;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import io.github.nucleuspowered.nucleus.api.exceptions.NucleusException;
import io.github.nucleuspowered.nucleus.api.exceptions.PluginAlreadyRegisteredException;
import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.Token;

@Plugin(id = Gluon.ID, name = Gluon.NAME, version = Gluon.VERSION, authors = Gluon.AUTHOR, description = Gluon.DESCRIPTION, dependencies = {
		@Dependency(id = "nucleus"), @Dependency(id = "placeholderapi") })
public class Gluon {

	final static String ID = "nucleus-gluon";
	final static String NAME = "Nucleus Gluon";
	final static String VERSION = "1.0.3";
	final static String DESCRIPTION = "A Nucleus - Placeholder API bridge.";
	final static String AUTHOR = "dualspiral";

	private final PluginContainer container;
	private NucleusMessageTokenService messageService;

	@Inject
	public Gluon(PluginContainer container) {
		this.container = container;
	}

	@Placeholder(id = "nucleus")
	public Text nucleus(@Source CommandSource source, @Token String token) throws NucleusException {
		if (messageService == null) {
			return null;
		}
		return messageService.createFromString(token).getForCommandSource(source);
	}

	@Listener
	public void onServiceRegisterEvent(ChangeServiceProviderEvent event) {
		// Get the services
		Optional<NucleusMessageTokenService> messageTokenService = Sponge.getServiceManager()
				.provide(NucleusMessageTokenService.class);
		Optional<PlaceholderService> placeholderService = Sponge.getServiceManager().provide(PlaceholderService.class);

		if (messageTokenService.isPresent() && placeholderService.isPresent()) {
			messageService = messageTokenService.get();
			Sponge.getServer().getConsole()
					.sendMessage(Text.of(TextColors.GREEN, Gluon.NAME, " version ", Gluon.VERSION));
			// Nucleus -> Placeholder API
			try {
				placeholderService.get().load(this, "nucleus", this).author(AUTHOR).version(VERSION)
						.tokens(messageService.getPrimaryTokens()).buildAndRegister();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Register this as a token parser.
			try {
				messageTokenService.get().register(this.container, new NucleusMessageTokenService.TokenParser() {
					private PlaceholderService service = placeholderService.get();

					@Nonnull
					@Override
					public Optional<Text> parse(String s, CommandSource commandSource, Map<String, Object> map) {
						if (!s.startsWith("nucleus_pl:" + Gluon.ID)) {
							return service.parse(s, commandSource, commandSource, Text.class);
						}
						return Optional.empty();
					}
				});

				// Register the token format.
				messageTokenService.get().registerTokenFormat("{%", "%}", "pl:" + Gluon.ID + ":$1");
			} catch (PluginAlreadyRegisteredException e) {
				e.printStackTrace();
			}

			// We're done here.
			Sponge.getEventManager().unregisterListeners(this);
		}
	}
}
