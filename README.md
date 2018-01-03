# standardcheckout-plugin
StandardCheckout Bukkit plugin

# Examples

## Starting a checkout flow that skips buycraft-related actions
```
public class Demo implements Listener {

	@EventHandler
	public void on(BlockBreakEvent event) {
		Player player = event.getPlayer();

		PurchaseFlow.builder()
				.name("Diamonds")
				.price(BigDecimal.valueOf(20))
				.callback(new PurchaseCallback() {
					@Override
					public void success(OfflinePlayer player) {
						Player online = player.getPlayer();
						if (online == null) {
							// you are responsible for handling this & making sure a player
							// gets their items. the chances of this being true are pretty slim,
							// but it could happen
							Bukkit.getLogger().info("player is offline");
							return;
						}

						Bukkit.getLogger().info("purchase success");
						online.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 64));
					}

					@Override
					public void failure(OfflinePlayer player) {
						Bukkit.getLogger().info("purchase failure");
						// The plugin will handle messaging automatically you
						// can add some special failure logic here if you so desire
					}
				}).begin(player);
	}

}

```

## Starting a checkout flow that communicates with buycraft
```
public class Example implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		PurchaseFlow.builder()
			.name("64 Diamonds")
			.withItem(1, 64) // assumes you have a buycraft package with an id of '1'
							// this is how price is calculated. additionally, any
							// commands on the buycraft package will be run on buycraft's
							// time via an automatic manual payment
			.callback(new PurchaseCallback() {
				@Override
				public void success(OfflinePlayer player) {
					Player online = player.getPlayer();
					if (online == null) {
						// you are responsible for handling this & making sure a player
						// gets their items. the chances of this being true are pretty slim,
						// but it could happen
						return;
					}

					online.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
				}

				@Override
				public void failure(OfflinePlayer player) {
					// The plugin will handle messaging automatically you
					// can add some special failure logic here if you so desire
				}
			})
			.begin(player);

		return false;
	}

}
```

## Resuming a flow when a player joins
```
public class Example implements Listener {

	@EventHandler
	public void on(PlayerJoinEvent event) {
		PurchaseFlow flow = PurchaseFlow.currentFlow(event.getPlayer());
		if (flow != null && !flow.isFinished()) {
			flow.resume();
		}
	}

}
```