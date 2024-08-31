-- 初始化 inventory 表格内的数据
DELETE FROM
    inventory;

INSERT INTO
    inventory (product_id, name, count)
VALUES
    ('AFA4474AEC7F420491CBE0BD5845431A', '鸡蛋', 18),
    ('0D8C7A19FD4346E7983FD3DC4DB75D68', '巧克力', 20),
    ('5015454E937B401A9316FBD61E9D5151', '苹果', 28),
    ('92F8D7B81CB54C569B37A64CE0CEF132', '啤酒', 21),
    ('9882D2F48A58434DBC7F828D2229BA90', '烤鸡', 24);

-- 初始化 product_order 表格内的数据
DELETE FROM
    product_order;

INSERT INTO
    product_order (order_id, product_id, name, count)
VALUES
    (
        'oder_0',
        'AFA4474AEC7F420491CBE0BD5845431A',
        '鸡蛋',
        1
    ),
    (
        'oder_1',
        '0D8C7A19FD4346E7983FD3DC4DB75D68',
        '巧克力',
        1
    ),
    (
        'oder_2',
        '5015454E937B401A9316FBD61E9D5151',
        '苹果',
        2
    ),
    (
        'oder_3',
        '92F8D7B81CB54C569B37A64CE0CEF132',
        '啤酒',
        3
    ),
    (
        'oder_4',
        '9882D2F48A58434DBC7F828D2229BA90',
        '烤鸡',
        4
    );