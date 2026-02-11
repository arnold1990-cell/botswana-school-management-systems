-- Align school setup master data with one school-scoped model.
DROP TABLE IF EXISTS academic_year CASCADE;

ALTER TABLE bus_routes
    ADD CONSTRAINT IF NOT EXISTS uq_bus_routes_school_name UNIQUE (school_id, name);
ALTER TABLE bus_ticket_types
    ADD CONSTRAINT IF NOT EXISTS uq_bus_ticket_types_school_name UNIQUE (school_id, name);
ALTER TABLE hostels
    ADD CONSTRAINT IF NOT EXISTS uq_hostels_school_name UNIQUE (school_id, name);
ALTER TABLE teams
    ADD CONSTRAINT IF NOT EXISTS uq_teams_school_name UNIQUE (school_id, name);
ALTER TABLE competitions
    ADD CONSTRAINT IF NOT EXISTS uq_competitions_school_name UNIQUE (school_id, name);
ALTER TABLE houses
    ADD CONSTRAINT IF NOT EXISTS uq_houses_school_name UNIQUE (school_id, name);
ALTER TABLE sports
    ADD CONSTRAINT IF NOT EXISTS uq_sports_school_name UNIQUE (school_id, name);
ALTER TABLE extra_mural_types
    ADD CONSTRAINT IF NOT EXISTS uq_extra_mural_types_school_name UNIQUE (school_id, name);
ALTER TABLE extra_mural_activities
    ADD CONSTRAINT IF NOT EXISTS uq_extra_mural_activities_school_name UNIQUE (school_id, name);
