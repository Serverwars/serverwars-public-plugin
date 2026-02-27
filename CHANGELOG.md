# Changelog

## 1.1.0

This update contains changes to the translation files. To get the most up-to-date default files, remove your local `plugins/Serverwars/lang` folder.

### Added
* Added `/sw` alias for the main `/serverwars` command.
* The lobby status is now displayed in the action bar to all participating players.
* New translations keys:
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