# Changelog

## 1.2.1

### Fixed
* Creating a Serverwars lobby no longer causes a short cooldown to enter the queue.

## 1.2.0

> This update contains changes to the config file. The most up-to-date `config.yml` can be found [here](https://github.com/Serverwars/serverwars-public-plugin/blob/main/src/main/resources/config.yml).

> This update contains changes to the translation files. The most up-to-date `plugins/Serverwars/lang` files can be found [here](https://github.com/Serverwars/serverwars-public-plugin/tree/main/src/main/resources/lang). 

### Added
* Added `server_ip` config option used to transfer players back after a game has finished.
* Added an inventory menu to more easily create and edit a Serverwars lobby. 
  * Added a command to open the menu: `/serverwars lobby`
  * Added a permission for the new command: `serverwars.commands.lobby.menu`
* New translation keys:
  * `command.error.no_lobby`: Shown when opening the menu when there is no lobby.
  * `command.error.no_permission`: Shown when user has no permission.
* Added a short cooldown after entering and leaving the match making queue.

## 1.1.0 (2026-02-27)

This update contains changes to the translation files. To get the most up-to-date default files, remove your local `plugins/Serverwars/lang` folder.

### Added
* Added `/sw` alias for the main `/serverwars` command.
* The lobby status is now displayed in the action bar to all participating players.
* New translation keys:
  * `lobby.status.action_bar`
  * `command.match.join.self.error.not_a_player`
  * `command.match.join.error.not_in_match`
  * `command.queue.leaving`

### Changed
* Changed some plugin chat messages to be more clear.
* Renamed queue translation keys:
  * `command.queue.enter.success -> command.queue.enter.success.notify_lobby`
  * `command.queue.leave.success -> command.queue.leave.success.notify_lobby`

### Fixed
* Fix checking if server is already in a match when entering queue.
* Fixed registering permission `serverwars.commands.lobby.set.game`.
* Fixed `/serverwars match enter` command always attempting to transfer the player to a match, even when there isn't any match active.

### Removed
* Removed serverwar lobby `size` argument. It can be derived from the amount of players that have joined the war.
  * Removed permission `serverwars.commands.lobby.set.size`
  * Removed translation keys:
    * `command.lobby.error.invalid_size`
    * `command.lobby.create.error.invalid_size`
    * `command.lobby.set.size.success` 
    * `command.lobby.set.size.success.notify_lobby`

## 1.0.2 (2026-02-25)

### Changed
* Lower required java version from 24 to 21.

### Fixed
* Change invite button icons to Unicode escapes.
* Fix player not found error when creating a lobby participant.

## 1.0.1 (2026-02-11)

### Fixed
* Players that have never logged out before can now join a lobby without issues.

## 1.0.0 (2026-01-11)

### Added
* Initial release