-- 修改 account 表的 student_id 列类型，从 VARCHAR 改为 BIGINT
ALTER TABLE account MODIFY student_id BIGINT;

-- 添加外键约束（如果尚未存在）
ALTER TABLE account 
ADD CONSTRAINT fk_account_student 
FOREIGN KEY (student_id) REFERENCES student(id);