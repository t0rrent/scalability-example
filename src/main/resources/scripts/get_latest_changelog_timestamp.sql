SELECT
	script_timestamp
FROM (
	SELECT
		row_number() OVER (ORDER BY script_timestamp DESC) AS rn,
		script_timestamp
	FROM changelog
) changelog_with_rn
WHERE rn = 1;
