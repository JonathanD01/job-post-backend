-- Insert values for JobPost
INSERT INTO jobposts (jobpost_id, created_at, url, company_name, company_image_url, image_url, title, description, deadline)
VALUES
(1, '2023-05-11', 'https://example.com/job1', 'Company A', 'https://example.com/images/company_a_logo.png', 'https://example.com/images/job1_image.png', 'Job Title 1', 'Description for Job 1', '2026-04-19'),
(2, '2023-05-11', 'https://example.com/job2', 'Company B', 'https://example.com/images/company_b_logo.png', 'https://example.com/images/job2_image.png', 'Job Title 2', 'Description for Job 2', null),
(3, '2023-05-11', 'https://example.com/job3', 'Company C', 'https://example.com/images/company_c_logo.png', 'https://example.com/images/job3_image.png', 'Job Title 3', 'Description for Job 3', null),
(4, '2023-05-11', 'https://example.com/job4', 'Company D', 'https://example.com/images/company_d_logo.png', 'https://example.com/images/job4_image.png', 'Job Title 4', 'Description for Job 4', null),
(5, '2023-05-11', 'https://example.com/job5', 'Company E', 'https://example.com/images/company_e_logo.png', 'https://example.com/images/job5_image.png', 'Job Title 5', 'Description for Job 5', null),
(6, '2023-05-11', 'https://example.com/job6', 'Company F', 'https://example.com/images/company_f_logo.png', 'https://example.com/images/job6_image.png', 'Job Title 6', 'Description for Job 6', '2026-05-12'),
(7, '2023-05-11', 'https://example.com/job7', 'Company G', 'https://example.com/images/company_g_logo.png', 'https://example.com/images/job7_image.png', 'Job Title 7', 'Description for Job 7', '2026-04-15'),
(8, '2023-05-11', 'https://example.com/job8', 'Company H', 'https://example.com/images/company_h_logo.png', 'https://example.com/images/job8_image.png', 'Job Title 8', 'Description for Job 8', '2026-04-28'),
(9, '2023-05-11', 'https://example.com/job9', 'Company I', 'https://example.com/images/company_i_logo.png', 'https://example.com/images/job9_image.png', 'Job Title 9', 'Description for Job 9', '2026-05-08'),
(10, '2023-05-11', 'https://example.com/job10', 'Company J', 'https://example.com/images/company_j_logo.png', 'https://example.com/images/job10_image.png', 'Job Title 10', 'Description for Job 10', '2026-04-19')
ON CONFLICT (jobpost_id) DO NOTHING;

-- Insert values for JobTag
INSERT INTO job_tags (tag)
VALUES
('Java'),
('Python'),
('JavaScript'),
('HTML'),
('CSS'),
('React'),
('Angular'),
('Node.js'),
('Spring Boot'),
('MongoDB')
ON CONFLICT (tag) DO NOTHING;

-- Insert values for JobDefinition
INSERT INTO job_definitions (key, value)
VALUES
('Location', 'Oslo'),
('Location', 'Stockholm'),
('Location', 'Copenhagen'),
('Category', 'Software Development'),
('Category', 'Web Development'),
('Category', 'Data Science'),
('Category', 'DevOps'),
('Category', 'Mobile Development'),
('Category', 'UI/UX Design'),
('Category', 'Project Management')
ON CONFLICT (key, value) DO NOTHING;

-- Insert values into the join table for JobPost and JobTag
INSERT INTO j_jobpost_tags (jobpost_id, jobtag_id)
VALUES
(1, 1),  -- JobPost 1 tags: Java
(2, 2),  -- JobPost 2 tags: Python
(3, 3),  -- JobPost 3 tags: JavaScript
(4, 4),  -- JobPost 4 tags: HTML
(5, 5),  -- JobPost 5 tags: CSS
(6, 6),  -- JobPost 6 tags: React
(7, 7),  -- JobPost 7 tags: Angular
(8, 8),  -- JobPost 8 tags: Node.js
(9, 9),  -- JobPost 9 tags: Spring Boot
(10, 10) -- JobPost 10 tags: MongoDB
ON CONFLICT (jobpost_id, jobtag_id) DO NOTHING;

-- Insert values into the join table for JobPost and JobDefinition
INSERT INTO j_jobpost_descriptions (jobpost_id, jobdescription_id)
VALUES
(1, 1),  -- JobPost 1 definitions: Location - Oslo
(2, 2),  -- JobPost 2 definitions: Location - Stockholm
(3, 3),  -- JobPost 3 definitions: Location - Copenhagen
(4, 4),  -- JobPost 4 definitions: Category - Software Development
(5, 5),  -- JobPost 5 definitions: Category - Web Development
(6, 6),  -- JobPost 6 definitions: Category - Data Science
(7, 7),  -- JobPost 7 definitions: Category - DevOps
(8, 8),  -- JobPost 8 definitions: Category - Mobile Development
(9, 9),  -- JobPost 9 definitions: Category - UI/UX Design
(10, 10)  -- JobPost 10 definitions: Category - Project Management
ON CONFLICT (jobpost_id, jobdescription_id) DO NOTHING;