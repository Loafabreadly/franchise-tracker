## Plan: Premier NHL Franchise Tracker TUI Expansion

Transform the existing Lanterna TUI into a comprehensive NHL franchise management tool featuring full roster/contract tracking, draft management, prospect development, FM-inspired visualizations, season archiving, and a franchise wrap-up summary—optimized for NHL video game franchise mode users.

### Steps

1. **Add Player Potential & Season Archive Models** — Extend [Player.java](src/main/java/com/github/loafabreadly/franchisetracker/model/Player.java) with `PLAYER_POTENTIAL` enum (Franchise, Elite, Top6F/Top4D, Top9F/Top6D, Bottom6F/BottomPairD, AHL) and goalie variants, plus `POTENTIAL_ACCURACY` (Exact, High, Med, Low); create new `SeasonSnapshot.java` model to store complete roster/stats state per season.

2. **Implement Season Archiving System** — Add `List<SeasonSnapshot>` to [FranchiseTracker.java](src/main/java/com/github/loafabreadly/franchisetracker/FranchiseTracker.java) with `archiveSeason()` method called on season advance; snapshot captures full roster, stats, contracts, awards, trades, and cap ceiling for that year.

3. **Build Contract & Cap Management System** — Create [ContractEditor.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/ContractEditor.java) for editing contracts and [CapDashboard.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/CapDashboard.java) showing cap hit, space, expiring contracts by year; add per-season `capCeiling` field configurable when advancing seasons.

4. **Implement Draft System UI** — Create [DraftManager.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/DraftManager.java) with views for: future pick inventory (`DraftPick`), draft history log (`DraftedPlayer`), and recording new selections linking picks to created prospects.

5. **Build Trade History & Award Tracking** — Create [TradeLog.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/TradeLog.java) for recording/viewing trades with assets exchanged, and [AwardManager.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/AwardManager.java) for logging player/team awards by season.

6. **Create Prospect Dashboard with Comparison View** — Create [ProspectTracker.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/ProspectTracker.java) showing AHL players + unsigned picks with potential/age/development; include side-by-side comparison mode displaying two prospects' attributes, stats, potential, and trajectory for call-up/send-down decisions.

7. **Add ASCII-Data Visualization Library** — Add `com.mitchtalmadge:ascii-data:1.4.0` to [build.gradle](build.gradle); create `ChartComponents.java` utility class with reusable methods for line graphs, bar charts, and sparklines integrated with Lanterna labels.

8. **Build Statistics Visualization Hub** — Create [StatsHub.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/StatsHub.java) with: player overall progression line graphs (priority), team points-per-season charts (priority), roster age distribution bars, cap trajectory timeline, and multi-player stat comparison views.

9. **Create Season History Browser** — Create [SeasonHistory.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/SeasonHistory.java) to browse archived snapshots—view past rosters, stats, awards, and trades for any completed season with navigation between years.

10. **Build Franchise Wrap-Up Summary** — Create [FranchiseWrapUp.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/FranchiseWrapUp.java) accessible via manual "Complete Franchise" action in main menu with confirmation dialog; generates comprehensive summary: total championships, all-time leaders, award history, best seasons, notable trades, drafted players who made NHL, and overall franchise grade.

11. **Enhance Player Editor with Full Attributes** — Extend [PlayerEditor.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/PlayerEditor.java) to include potential tier dropdown, accuracy modifier, X-Factor multi-select, play style selection, and inline contract quick-edit.

12. **Add AHL Stats & CSV/JSON Import** — Modify [SeasonStatsEditor.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/SeasonStatsEditor.java) for NHL/AHL toggle with `leagueLevel` field in stats models; create [ImportExport.java](src/main/java/com/github/loafabreadly/franchisetracker/scene/ImportExport.java) for power-user CSV/JSON roster import with documented template.
