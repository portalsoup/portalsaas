CREATE TABLE if NOT EXISTS blog_post_attachment (
  blog_post_id INT,
  attachment_id INT,
  PRIMARY KEY (blog_post_id, attachment_id),
  CONSTRAINT fk_blog_post FOREIGN KEY(blog_post_id) REFERENCES blog_post(id),
  CONSTRAINT fk_attachment FOREIGN KEY(attachment_id) REFERENCES attachment(id)
);