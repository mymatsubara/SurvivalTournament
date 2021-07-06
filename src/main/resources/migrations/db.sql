CREATE TABLE IF NOT EXISTS team (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    points INTEGER NOT NULL DEFAULT 0,
    spawn_x INTEGER,
    spawn_z INTEGER,
    spawn_y INTEGER
);

CREATE TABLE IF NOT EXISTS player (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    team_id INTEGER NOT NULL,
    is_leader INTEGER NOT NULL DEFAULT 0,
    points INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE IF NOT EXISTS objective_type (
  id INTEGER PRIMARY KEY,
  display_name TEXT NOT NULL,
  criteria TEXT NOT NULL UNIQUE,
  type TEXT NOT NULL,
  description TEXT,
  points INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS objective (
    objective_type_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    count INTEGER NOT NULL,
    points INTEGER NOT NULL,
    PRIMARY KEY (objective_type_id, player_id),
    FOREIGN KEY (objective_type_id) REFERENCES objective_type(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES player(id)
);

CREATE TRIGGER IF NOT EXISTS update_objective_points
	AFTER UPDATE
	ON objective
	WHEN NEW.points != OLD.points
BEGIN
	UPDATE player
	SET points = (SELECT SUM(points) FROM objective WHERE player_id = NEW.player_id);

	UPDATE team
	SET points = (SELECT SUM(points) from player
		WHERE team_id IN(SELECT team_id FROM player WHERE id = NEW.player_id));
END;

CREATE TRIGGER IF NOT EXISTS insert_objective_points
	AFTER INSERT
	ON objective
BEGIN
	UPDATE player
	SET points = (SELECT SUM(points) FROM objective WHERE player_id = NEW.player_id);

	UPDATE team
	SET points = (SELECT SUM(points) from player
		WHERE team_id IN(SELECT team_id FROM player WHERE id = NEW.player_id));
END;