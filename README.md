# standardcheckout-plugin
StandardCheckout Bukkit plugin

Maven dependency (mvn install this first): https://github.com/UlfricProjects/buycraft

# Examples

## Starting a checkout flow that skips buycraft-related actions
```
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
			Bukkit.getLogger().info("purchase failure - either because they need to authorize, or their card failed");
			// The plugin will handle messaging automatically you
			// can add some special failure logic here if you so desire
		}
	}).begin(Bukkit.getPlayer("Packet"));
```

## Starting a checkout flow that communicates with buycraft
```
PurchaseFlow.builder()
	.name("64 Diamonds")
	.withItem(1, 64) // assumes you have a buycraft package with an id of '1'
			// and you want to give the player 64 of that package.
			// This uses buycraft for price calculation. additionally, any
			// commands on the buycraft package will be run on buycraft's
			// time via an automatic manual payment. Be careful with the quantity,
			// it will create x amount of manual payments through the buycraft api.
			// They don't support quantities in the manual payment logic.
	.callback(PurchaseCallback.success(offlinePlayer -> { // uses a lambda, the failure method is ignored
		Player online = player.getPlayer();
		if (online == null) {
			// you are responsible for handling this & making sure a player
			// gets their items. the chances of this being true are pretty slim,
			// but it could happen
			return;
		}

		online.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
	})
	.begin(player);
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
