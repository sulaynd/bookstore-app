
--TRUNCATE TABLE ... CASCADE removes all rows from a specified table and automatically
--truncates any other tables that have a foreign key reference to it, as long as those
--references are set up with an ON DELETE CASCADE constraint.
--This command is useful for quickly clearing data in a parent-child table relationship
--without manually deleting rows from the dependent child tables first.

truncate table orders cascade;
alter sequence order_id_seq restart with 100;
alter sequence order_item_id_seq restart with 100;

insert into orders (id,order_number,username,
                    customer_name,customer_email,customer_phone,
                    delivery_address_line1,delivery_address_line2,delivery_address_city,
                    delivery_address_state,delivery_address_zip_code,delivery_address_country,
                    status,comments) values
(1, 'order-123', 'dieg', 'Dieg', 'user@gmail.com', '11111111', '123 Main St', 'Apt 1', 'Dallas', 'TX', '75001', 'USA', 'NEW', null),
(2, 'order-456', 'dieg', 'Dieg', 'sad@gmail.com', '2222222', '123 Main St', 'Apt 1', 'Hyderabad', 'TS', '500072', 'India', 'NEW', null)
;

insert into order_items(order_id, code, name, price, quantity) values
(1, 'P100', 'The Hunger Games', 34.0, 2),
(1, 'P101', 'To Kill a Mockingbird', 45.40, 1),
(2, 'P102', 'The Chronicles of Narnia', 44.50, 1)
;
