-- Remove unique constraint from academic_years.year
ALTER TABLE academic_years
    DROP CONSTRAINT IF EXISTS academic_years_year_ukhoxaidjaabmvfm0ugpwi40mjm;



SELECT conname
FROM pg_constraint
WHERE conrelid = 'academic_years'::regclass
  AND contype = 'u';


